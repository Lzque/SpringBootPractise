package com.xsc.domain;

import lombok.Data;

@Data
public class SelectBookCondition {
    private String bookName;
    private Double priceStart;
    private Double priceEnd;
    private String sort;
    private Boolean onlyInStock;
}
