package cn.yumietv.service.impl;

import cn.yumie.service.AnimeEpisodesService;
import cn.yumietv.entity.AnimeEpisodes;
import cn.yumietv.mapper.AnimeMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.yumietv.mapper.AnimeEpisodesMapper;

import java.util.List;

@Service
public class AnimeEpisodesServiceImpl implements AnimeEpisodesService {
    @Autowired
    private AnimeEpisodesMapper animeEpisodesMapper;
    @Autowired
    private AnimeMapper animeMapper;

    public Integer getEpisodesCountById(long animeId) {
        QueryWrapper<AnimeEpisodes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eid", animeId);
        int episodesCount = animeEpisodesMapper.selectCount(queryWrapper);
        return episodesCount;
    }

    public AnimeEpisodes getEpisodeByFileName(String fileName) {
        QueryWrapper<AnimeEpisodes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", fileName);
        AnimeEpisodes animeEpisodes = animeEpisodesMapper.selectOne(queryWrapper);
        return animeEpisodes;
    }

}
