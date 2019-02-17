package cn.yumie.service;

import cn.yumietv.domain.AnimeAndEpisodes;

import java.util.List;

public interface AnimeCategoryService {
    List<AnimeAndEpisodes> getSimilar(long animeId, List<String> cateList);
    List<String> getAnimeCategoryById(long animeId);
}
