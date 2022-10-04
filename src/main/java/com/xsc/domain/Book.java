package com.xsc.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Book {
    private Integer id;
    private String bookName;
    private String author;// 作者
    private String publishingHouse;// 出版社
    private String sort;// 类别
    private Double price;// 价格
    private Integer inStock;// 初始库存
    private Integer sales;// 销量
    private Integer userId;// 卖家
    @TableField(exist = false)
    private String sellerUserName; // 卖家名
    private Double overallRating;// 图书综合评分
    private byte[] coverData; // 存储图书封面
    @TableField(exist = false)
    private String imagePath;// 用于放置图书的某一张图片的Base64，用作展示，不在数据库中
    @TableField(exist = false)
    private List<String> bookImageBase64s;// 用于放置图书的某一张图片的Base64，用作展示，不在数据库中
    @TableField(exist = false)
    private Integer commentQuantity; // 评论数
    @TableLogic(value = "0", delval = "1")
    private Boolean takeDownBook;//'是否下架图书'
}
