package com.test.eigen.book;

import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String code;
    private String title;
    private String author;
    private int stock;
}
