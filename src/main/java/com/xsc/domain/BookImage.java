package com.xsc.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookImage {
    private Integer id;
    private Integer bookId;
    private byte[] image;
    @TableField(exist = false)
    private String base64String;
}
