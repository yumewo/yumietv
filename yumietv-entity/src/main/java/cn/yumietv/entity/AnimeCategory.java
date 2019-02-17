package cn.yumietv.entity;

import lombok.Data;

@Data
public class AnimeCategory {
    private Long cid;

    private String category;

    public Long getCid() {
        return cid;
    }

}