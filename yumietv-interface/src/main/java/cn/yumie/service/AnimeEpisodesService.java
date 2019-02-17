package cn.yumie.service;

import cn.yumietv.entity.AnimeEpisodes;

public interface AnimeEpisodesService {
    Integer getEpisodesCountById(long animeId);
    AnimeEpisodes getEpisodeByFileName(String fileName);
}
