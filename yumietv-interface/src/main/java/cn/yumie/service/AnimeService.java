package cn.yumie.service;

import cn.yumietv.domain.AnimeAndEpisodes;
import cn.yumietv.entity.Anime;

import java.util.List;

public interface AnimeService {
    AnimeAndEpisodes getAnimeByTitle(Long id);
    List<Anime> getIsEndAnime();
    List<Anime> getNewAnime();
    List<Anime> getHotAnime();
    String getImgUrl(Long id);
}
