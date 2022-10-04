package com.xsc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xsc.domain.Book;
import com.xsc.domain.BuyBook;
import com.xsc.domain.Shopping;
import com.xsc.domain.User;
import com.xsc.mapper.BookMapper;
import com.xsc.mapper.BuyBookMapper;
import com.xsc.mapper.ShoppingMapper;
import com.xsc.mapper.UserMapper;
import com.xsc.service.ShoppingCart;
import com.xsc.utils.ImageHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ShoppingCartImpl implements ShoppingCart {
    @Autowired
    private BuyBookMapper buyBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private ShoppingMapper shoppingMapper;

    @Override
    public List<Shopping> bookAddOnLoad(Integer userId) {
        LambdaQueryWrapper<Shopping> shoppingWrapper = new LambdaQueryWrapper<>();
        shoppingWrapper.eq(Shopping::getUserId, userId).orderByDesc(Shopping::getId);
        List<Shopping> shoppingList = shoppingMapper.selectList(shoppingWrapper);
        for (Shopping shopping : shoppingList) {
            Book book = bookMapper.selectById(shopping.getBookId());
            // 水印
            if (book.getInStock() - book.getSales() <= 0) {
                book.setCoverData(ImageHandle.addSoldOutWatermark(book.getCoverData()));
            }
            book.setImagePath("data:image/png;base64," + Base64.getEncoder().encodeToString(book.getCoverData()));
            // 库存和加购数的同步，加购数不能大于库存
            if (shopping.getShoppingQuantity() > book.getInStock() - book.getSales()) {
                LambdaUpdateWrapper<Shopping> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Shopping::getId, shopping.getId()).set(Shopping::getShoppingQuantity, book.getInStock() - book.getSales());
                shoppingMapper.update(null, updateWrapper);
            }
            shopping.setBook(book);
        }
        return shoppingList;
    }

    @Override
    public void deleteAddOnBook(Integer id) {
        shoppingMapper.deleteById(id);
    }

    @Override
    public void deleteSelectedBook(List<Integer> addOnIds) {
        shoppingMapper.deleteBatchIds(addOnIds);
    }

    @Override
    public void updateShoppingQuantity(Integer id, Integer newShoppingQuantity) {
        LambdaUpdateWrapper<Shopping> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Shopping::getId, id).set(Shopping::getShoppingQuantity, newShoppingQuantity);
        shoppingMapper.update(null, wrapper);
    }

    @Override
    public String purchaseBook(List<Shopping> shoppingList, Boolean deliveryMethod, Double bookPrice) {
        // 查看是否开启钱包,查看账户余额是否够
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, shoppingList.get(0).getUserId()).select(User::getBalance, User::getWallet);
        User user = userMapper.selectOne(queryWrapper);
        if (!user.getWallet()) {
            return "未开通钱包，请前往个人中心开通";
        } else if (user.getBalance() < bookPrice) {
            return "账户余额不足，请充值";
        } else {
            Double balance = user.getBalance() - bookPrice;
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, shoppingList.get(0).getUserId()).set(User::getBalance, balance);
            userMapper.update(null, updateWrapper);
        }/*后续确认收货再把商品得到的钱汇到卖家账户(购书表每条记录执行一次操作，平台抽成5%到一指定账户)，并对送货上门的商品抽成1元*/
        // 把数据添加到购书表中，并更改图书表图书销量
        BuyBook buyBook = new BuyBook();
        List<Integer> deleteIds = new ArrayList<>();
        for (Shopping shopping : shoppingList) {
            buyBook.setUserId(shopping.getUserId());
            buyBook.setBookId(shopping.getBookId());
            buyBook.setPurchaseQuantity(shopping.getShoppingQuantity());
            buyBook.setDelivery(deliveryMethod);
            buyBook.setId(null);
            buyBook.setComment("用户还没有评论");
            buyBookMapper.insert(buyBook);
            deleteIds.add(shopping.getId());
            // 更改图书表
            LambdaQueryWrapper<Book> bookQueryWrapper = new LambdaQueryWrapper<>();
            bookQueryWrapper.eq(Book::getId, shopping.getBookId()).select(Book::getSales);
            Book selectOneBook = bookMapper.selectOne(bookQueryWrapper);
            LambdaUpdateWrapper<Book> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Book::getId, shopping.getBookId()).set(Book::getSales, selectOneBook.getSales() + shopping.getShoppingQuantity());
            bookMapper.update(null, updateWrapper);
        }
        // 清除加购表数据
        shoppingMapper.deleteBatchIds(deleteIds);
        return "图书购买成功，详情可到已购图片页面查看";
    }

    @Override
    public Boolean comparePassword(String enterPaymentPassword, Integer userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userId).eq(User::getPaymentPassword, enterPaymentPassword);
        User user = userMapper.selectOne(wrapper);
        return user != null;
    }

    @Override
    public Boolean addressConfirmation(Integer userId) {
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        //.select(User::getAddress)出问题，给数据库address字段默认值又没事了
        userQueryWrapper.eq(User::getId, userId);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user.getAddress() == null) return false;
        return !(user.getAddress().equals("无") || user.getAddress().equals(""));
    }
}
