package cn.yumietv.mapper;

import cn.yumietv.domain.SearchAnime;

import java.util.List;

public interface SearchAnimeMapper {
    List<SearchAnime> getAnimeList();

    List<String> selectDuoCategory();

    List<String> zimu24();

    List<String> getNianFen();

    List<String> getCountry();
}
