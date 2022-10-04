package com.xsc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Base64AndByteArray {
    private byte[] bytes;
    private String base64String;
}
