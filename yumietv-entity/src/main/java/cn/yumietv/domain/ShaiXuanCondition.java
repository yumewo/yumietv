package cn.yumietv.domain;

import lombok.Data;

import java.util.List;

/**
 * @Auther: yumie
 * @Date: 2019/1/19 19:17
 * @Description: 进行筛选的各个条件
 */
@Data
public class ShaiXuanCondition {
    //时间
    private String sj;
    //地区
    private String dq;
    //状态
    private String zt;
    //类型
    private String lx;
    //首字母
    private String szm;
    //当前页
    private Integer currentPage = 1;
    //每页显示条数
    private Integer rows = 10;
    //总页数
    private Integer countPage;
    //前三页
    private Integer qiansanye;
    //后三页
    private Integer housanye;
    //结果列表
    private List<SearchAnime> searchAnimeList;
}
