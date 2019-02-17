package cn.yumietv.entity;

import lombok.Data;

@Data
public class Anime {
    private Long id;

    private String title;

    private String createDate;

    private String country;

    private String actors;

    private String director;

    private String imgUrl;

    private String tip;

    private String isEnd;

    private Integer status;

    private String firstWord;

    private String updateDate;
}