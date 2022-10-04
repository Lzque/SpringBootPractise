package com.xsc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// 评论图片
public class CommentImage {
    private Integer id;
    private Integer buyBookId;
    private byte[] Image;
}
