package com.xsc.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BuyBook {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer purchaseQuantity;// 购买数量
    private Boolean delivery;// 是否送货上门
    private Boolean receipt;//  是否收货
    private Boolean commentStatus;// 是否评论
    private Integer rating;// 评分
    private String comment;// 评论
    @TableField(exist = false)
    private Book book;
    @TableField(exist = false)
    private List<String> commentImageBase64s;
    @TableField(exist = false)
    private User user;
}
