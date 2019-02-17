package cn.yumietv.domain;

import cn.yumietv.entity.Anime;
import cn.yumietv.entity.AnimeDesc;
import cn.yumietv.entity.AnimeEpisodes;
import lombok.Data;
import org.apache.solr.common.StringUtils;

import java.util.List;

@Data
public class AnimeAndEpisodes {
    private Long id;

    private String title;

    private String CreateDate;

    private String country;

    private String actors;

    private String director;

    private String imgUrl;

    private String isEnd;

    private Integer status;

    private String firstWord;

    private List<AnimeEpisodes> episodes;

    private List<String> categories;

    private AnimeDesc desc;

    private Integer episodesCount;

    public void setActors(String actors) {
        this.actors = actors;
    }

    //用空串切割返回数组
    public String[] getActorss() {
        if (StringUtils.isEmpty(actors)) {
            return null;
        }
        String[] split = actors.split(" ");
        return split;
    }

    public String[] getDirectors() {
        if (StringUtils.isEmpty(director)) {
            return null;
        }
        String[] split = director.split(" ");
        return split;
    }

    public AnimeAndEpisodes(Anime anmie) {
        super();
        this.id = anmie.getId();
        this.title = anmie.getTitle();
        this.CreateDate = anmie.getCreateDate();
        this.country = anmie.getCountry();
        this.actors = anmie.getActors();
        this.director = anmie.getDirector();
        this.imgUrl = anmie.getImgUrl();
        this.isEnd = anmie.getIsEnd();
        this.status = anmie.getStatus();
        this.firstWord = anmie.getFirstWord();
    }

}
