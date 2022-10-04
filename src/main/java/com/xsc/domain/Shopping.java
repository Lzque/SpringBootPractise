package com.xsc.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Shopping {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer shoppingQuantity;
    @TableField(exist = false)
    private Book book;
}
