package com.xsc.controller;

import com.xsc.domain.Base64AndByteArray;
import com.xsc.domain.User;
import com.xsc.exception.BusinessException;
import com.xsc.exception.SystemException;
import com.xsc.protocol.Code;
import com.xsc.protocol.Result;
import com.xsc.service.RegisterAndLogIn;
import com.xsc.utils.ImageHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/registerAndLogIn")
public class RegisterAndLogInController {
    @Autowired
    private RegisterAndLogIn registerAndLogIn;

    @PostMapping("/imageBase64")
    public Result ImageBase64(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String base64String = Base64.getEncoder().encodeToString(bytes);
            return new Result(new Base64AndByteArray(bytes, base64String), Code.OK, "图片上传成功");
        } catch (IOException e) {
            throw new SystemException(Code.SYSTEM_ERR, "图片上传失败", e);
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        user.setAvatarData(ImageHandle.imageCompression(user.getAvatarData()));
        boolean register = this.registerAndLogIn.register(user);
        if (register) {
            return new Result(Code.OK, "注册成功");
        } else {
            return new Result(Code.ERR, "注册失败，号码已注册");
        }
    }

    @PostMapping("/logIn")
    public Result logIn(@RequestBody User user, HttpServletRequest request) {
        Integer logInId = registerAndLogIn.logIn(user);
        if (logInId != -1) {
            request.getSession().setAttribute("id", logInId);
            return new Result(Code.OK, "登录成功");
        } else {
            return new Result(Code.ERR, "登录失败，账号或密码错误");
        }
    }
}
