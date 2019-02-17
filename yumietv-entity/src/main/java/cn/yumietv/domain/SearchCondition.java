package cn.yumietv.domain;

import lombok.Data;

import java.util.List;

/**
 * @Auther: yumie
 * @Date: 2019/1/23 18:42
 * @Description:
 */
@Data
public class SearchCondition {
    //搜索的字符串
    private String str;
    //当前页
    private Integer currentPage = 1;
    //每页显示条数
    private Integer rows = 4;
    //总页数
    private Integer countPage;
    //前三页
    private Integer qiansanye;
    //后三页
    private Integer housanye;
    //查询结果集合
    private List<SearchAnime> searchAnimeList;
}
