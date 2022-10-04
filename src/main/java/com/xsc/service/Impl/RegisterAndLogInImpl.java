package com.xsc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xsc.domain.User;
import com.xsc.exception.BusinessException;
import com.xsc.mapper.UserMapper;
import com.xsc.protocol.Code;
import com.xsc.service.RegisterAndLogIn;
import com.xsc.utils.ProjectPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class RegisterAndLogInImpl implements RegisterAndLogIn {
    @Autowired
    private UserMapper userMapper;

    //
    @Override
    public byte[] saveImage(MultipartFile file) {
       /* byte[] bytes;
        // 拿到上传时的文件名
        String fileName = file.getOriginalFilename();
        // 获取图片后缀 jpg png jpeg 统一命名
        assert fileName != null;
        int index = fileName.lastIndexOf(".");
        fileName = "xsc" + fileName.substring(index);
        String projectPath = ProjectPath.projectPath();
        File f = new File(projectPath + "\\src\\main\\resources\\static\\temp\\" + fileName);//指定图片在服务器端的位置
        try {
            bytes = file.getBytes();
            file.transferTo(f); //把图片保存到服务器中
        } catch (IOException e) {
            throw new BusinessException(Code.BUSINESS_ERR, "图片上传失败", e);
        }
        return bytes;*/
        return null;
    }

    @Override
    public boolean register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhoneNumber, user.getPhoneNumber());
        Integer count = userMapper.selectCount(wrapper);
        if (count > 0) return false;
        int insert = userMapper.insert(user);
        return insert > 0;
    }

    @Override
    public Integer logIn(User user) {
        // 后续加一个在线状态的查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhoneNumber, user.getPhoneNumber());
        wrapper.eq(User::getUserPassword, user.getUserPassword());
        wrapper.select(User::getId);
        User selectOneUser = userMapper.selectOne(wrapper);
        return selectOneUser != null ? selectOneUser.getId() : -1;
    }

}
