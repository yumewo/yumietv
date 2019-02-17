package cn.yumietv.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.yumie.service.AnimeCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.yumietv.domain.AnimeAndEpisodes;
import cn.yumietv.entity.Anime;
import cn.yumietv.entity.AnimeCategory;
import cn.yumietv.mapper.AnimeCategoryMapper;
import cn.yumietv.mapper.AnimeMapper;
@Service
public class AnimeCategoryServiceImpl implements AnimeCategoryService {
    @Autowired
    private AnimeCategoryMapper animeCategoryMapper;
    @Autowired
    private AnimeMapper animeMapper;
    @Autowired
    private AnimeEpisodesServiceImpl animeEpisodesServiceImpl;


    /*
     * 根据动漫ID查询数据库推荐相似的作品
     */

    public List<AnimeAndEpisodes> getSimilar(long animeId, List<String> cateList) {
        Map<Long, Integer> map = new HashMap<>();
        // 遍历所有类型逐个查询该分类的Anime
        for (String cate : cateList) {
            QueryWrapper<AnimeCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("category", cate);
            List<AnimeCategory> animeCategories = animeCategoryMapper.selectList(queryWrapper);
            // 存入map,值使用Integer计数
            for (AnimeCategory AnimeCategory : animeCategories) {
                // 不推荐当前的影视
                if (!AnimeCategory.getCid().equals(animeId)) {
                    // 如果为空加进去,不为空count+1
                    Integer count = 1;
                    if (map.get(AnimeCategory.getCid()) != null) {
                        count = map.get(AnimeCategory.getCid()) + 1;
                    }
                    map.put(AnimeCategory.getCid(), count);
                }

            }
        }
        // 将map按value排序取出前五个的ID
        Map<Long, Integer> sortedMap = new LinkedHashMap<Long, Integer>();

        List<Entry<Long, Integer>> lists = new ArrayList<Entry<Long, Integer>>(map.entrySet());
        Collections.sort(lists, new Comparator<Entry<Long, Integer>>() {

            @Override
            public int compare(Entry<Long, Integer> o1, Entry<Long, Integer> o2) {
                Integer q1 = o1.getValue();
                Integer q2 = o2.getValue();
                Integer p = q2 - q1;
                if (p > 0)
                    return 1;
                else if (p == 0)
                    return 0;
                else
                    return -1;
            }

        });

        if (lists.size() >= 5) {
            // lists.subList()用法
            for (Entry<Long, Integer> set : lists.subList(0, 5)) {
                sortedMap.put(set.getKey(), set.getValue());
            }
        } else {
            for (Entry<Long, Integer> set : lists) {
                sortedMap.put(set.getKey(), set.getValue());
            }
        }
        // 再根据id查询Anime信息
        List<AnimeAndEpisodes> list = new ArrayList<>();
        // 效率高,以后一定要使用此种方式
        Iterator<Entry<Long, Integer>> iter = sortedMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, Integer> entry = (Entry<Long, Integer>) iter.next();
            Long key = entry.getKey();
            Anime anime = animeMapper.selectOne(new QueryWrapper<Anime>().eq("id", key));
            AnimeAndEpisodes animeAndEpisodes = new AnimeAndEpisodes(anime);
            Integer episodesCount = animeEpisodesServiceImpl.getEpisodesCountById(anime.getId());
            animeAndEpisodes.setEpisodesCount(episodesCount);
            list.add(animeAndEpisodes);

        }
        for (AnimeAndEpisodes animeAndEpisodes : list) {
            Integer count = animeEpisodesServiceImpl.getEpisodesCountById(animeAndEpisodes.getId());
            if (animeAndEpisodes.getIsEnd().contains("完")) {
                if (count == 1 && animeAndEpisodes.getIsEnd().contains("剧场")) {
                    animeAndEpisodes.setIsEnd("剧场版");
                } else {
                    animeAndEpisodes.setIsEnd("共" + count + "集");
                }
            } else {
                animeAndEpisodes.setIsEnd("更新到" + count + "集");
            }
        }
        return list;

    }

    public List<String> getAnimeCategoryById(long animeId) {
        // 根据Id查询包含的所有类型
        QueryWrapper<AnimeCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", animeId);
        List<AnimeCategory> cateList = animeCategoryMapper.selectList(queryWrapper);
        List<String> list = new ArrayList<>();
        for (AnimeCategory category : cateList) {
            list.add(category.getCategory());
        }
        return list;
    }
}
