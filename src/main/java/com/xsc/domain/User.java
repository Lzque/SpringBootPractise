package com.xsc.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Integer id;
    private String userName;   // 用户昵称
    private String phoneNumber; // 电话号码
    private String userPassword; // 登陆密码
    private String address; // 地址
    private Boolean wallet; // 是否开通钱包
    private Double balance; // 余额
    private String paymentPassword; // 支付密码
    private Boolean merchant; // 是否是商家
    private byte[] avatarData; // 头像字节数据
    @TableLogic(value = "0", delval = "1")
    private Boolean logOut; // 是否注销
    @TableField(exist = false)
    private String avatarImagePath;
}
