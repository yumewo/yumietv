package cn.yumietv.domain;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.StringUtils;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Auther: yumie
 * @Date: 2019/1/17 20:03
 * @Description: 对应solr数据集的实体
 */

@Data
public class SearchAnime implements Serializable {
    @Field("id")
    private String solrId = UUID.randomUUID().toString();

    @Field("anime_id")
    private Long id;

    @Field("anime_title")
    private String title;

    @Field("anime_actors")
    private String actors;

    @Field("anime_first_word")
    private String firstWord;

    @Field("anime_image")
    private String imgUrl;

    @Field("anime_category")
    private String category;

    @Field("anime_createDate")
    private String createDate;

    @Field("anime_country")
    private String country;

    @Field("anime_director")
    private String director;

    @Field("anime_status")
    private String status;

    @Field("anime_isEnd")
    private String isEnd;

    @Field("anime_desc")
    private String animeDesc;

    @Field("anime_updateDate")
    private String updateDate;

    private String gjz;

    public String getUpdateNYR() {
        return category.split(" ")[0];
    }

    public String[] getCategories() {
        return category.split(" ");
    }

    public String[] getDirectors() {
        if (StringUtils.isEmpty(director)) {
            return null;
        }
        if (director.contains("span")) {
            return splitHaveCss(director);
        }
        return director.split(" ");
    }

    public String[] getActorss() {
        if (StringUtils.isEmpty(actors)) {
            return null;
        }
        if (actors.contains("span")) {
            return splitHaveCss(actors);
        }
        return actors.split(" ");
    }

    String[] splitHaveCss(String str) {
        str = str.replace("<span style='color:red'>", "").replace("</span>", "");
        String[] split = str.split(" ");
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains(gjz)) {
                if (i == 0) {
                    split[i] = split[i].replace(gjz, "<span style='color:red'>" + gjz + "</span>");
                    continue;
                }
                String zjs = split[0];
                split[0] = split[i].replace(gjz, "<span style='color:red'>" + gjz + "</span>");
                split[i] = zjs;
            }
        }
        return split;
    }
}
