package cn.yumietv.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.yumie.service.AnimeService;
import cn.yumietv.entity.*;
import cn.yumietv.mapper.AnimeCategoryMapper;
import cn.yumietv.utils.FastJsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.yumietv.domain.AnimeAndEpisodes;
import cn.yumietv.mapper.AnimeDescMapper;
import cn.yumietv.mapper.AnimeEpisodesMapper;
import cn.yumietv.mapper.AnimeMapper;

/*
 * 动漫详细页面管理Service
 */
@Service
public class AnimeServiceImpl implements AnimeService {
    @Autowired
    private AnimeMapper animeMapper;
    @Autowired
    private AnimeEpisodesMapper animeEpisodesMapper;
    @Autowired
    private AnimeDescMapper animeDescMapper;
    @Autowired
    private AnimeCategoryMapper animeCategoryMapper;
    @Autowired
    private AnimeEpisodesServiceImpl animeEpisodesServiceImpl;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @return cn.yumietv.domain.AnimeAndEpisodes
     * @Author yumie
     * @Description //TODO 从多个表中获取动漫的详情信息
     * @Date 18:53 2019/1/16
     * @Param [animeId] 根据ID查询
     **/

    public AnimeAndEpisodes getAnimeByTitle(Long id) {
        // 根据id查询电视信息和播放链接
        Anime anime = animeMapper.selectOne(new QueryWrapper<Anime>().eq("id", id));
        AnimeAndEpisodes animeAndEpisodes = new AnimeAndEpisodes(anime);
        //分类
        List<AnimeCategory> animeCategories = animeCategoryMapper.selectList(new QueryWrapper<AnimeCategory>().eq("cid", id));
        List<String> cates = new ArrayList<>();
        for (AnimeCategory animeCategory : animeCategories) {
            cates.add(animeCategory.getCategory());
        }
        animeAndEpisodes.setCategories(cates);
        //播放链接
        List<AnimeEpisodes> episodes = animeEpisodesMapper.selectList(new QueryWrapper<AnimeEpisodes>().eq("eid", id));
        episodes = paixujuji(episodes);
        animeAndEpisodes.setEpisodes(episodes);
        //简介
        List<AnimeDesc> desc = animeDescMapper.selectList(new QueryWrapper<AnimeDesc>().eq("did", id));
        animeAndEpisodes.setDesc(desc.get(0));
        return animeAndEpisodes;
    }

    //还没做热度,随便取了
    public List<Anime> getHotAnime() {
        List<Anime> animeList = redisIsExist("hotAnime");
        if (animeList != null && animeList.size() > 0) {
            return animeList;
        }
        Page<Anime> p = new Page<>(2, 10);
        animeList = animeMapper.selectPage(p, new QueryWrapper<Anime>()).getRecords();
        animeList = gengxinOrgong(animeList);
        //存入缓存
        redisTemplate.opsForValue().set("hotAnime", FastJsonUtil.list2Json(animeList));
        return animeList;
    }

    /**
     * @return java.util.List<cn.yumietv.entity.Anime>
     * @Author yumie
     * @Description //TODO 获取前十个已完结的动漫
     * @Date 18:56 2019/1/16
     * @Param []
     **/

    public List<Anime> getIsEndAnime() {
        List<Anime> animeList = redisIsExist("endAnime");
        if (animeList != null && animeList.size() > 0) {
            return animeList;
        }
        QueryWrapper<Anime> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("is_end", "完");
        Page<Anime> p = new Page<>(1, 10);
        IPage<Anime> animeIPage = animeMapper.selectPage(p, queryWrapper);
        animeList = animeMapper.selectPage(p, queryWrapper).getRecords();
        animeList = gengxinOrgong(animeList);
        //存入缓存
        redisTemplate.opsForValue().set("endAnime", FastJsonUtil.list2Json(animeList));
        return animeList;
    }

    /**
     * @return java.util.List<cn.yumietv.entity.Anime>
     * @Author yumie
     * @Description //TODO 获取最新的十部动漫
     * @Date 18:56 2019/1/16
     * @Param []
     **/

    public List<Anime> getNewAnime() {
        List<Anime> animeList = redisIsExist("newAnime");
        if (animeList != null && animeList.size() > 0) {
            return animeList;
        }
        QueryWrapper<Anime> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_date");
        Page<Anime> p = new Page<>(1, 10);
        animeList = animeMapper.selectPage(p, queryWrapper).getRecords();
        animeList = gengxinOrgong(animeList);
        //存入缓存
        redisTemplate.opsForValue().set("newAnime", FastJsonUtil.list2Json(animeList));
        return animeList;
    }

    /**
     * @return java.util.List<cn.yumietv.entity.Anime>
     * @Author yumie
     * @Description //TODO 将动漫List进行循环,根据条件更改isEnd属性,返回新的List
     * @Date 18:58 2019/1/16
     * @Param [animeList]
     **/

    public List<Anime> gengxinOrgong(List<Anime> animeList) {
        for (Anime anime : animeList) {
            Integer count = animeEpisodesServiceImpl.getEpisodesCountById(anime.getId());
            if (anime.getIsEnd().contains("完")) {
                List<AnimeEpisodes> episodesList = animeEpisodesMapper.selectList(new QueryWrapper<AnimeEpisodes>().eq("eid", anime.getId()));
                if (episodesList.size() == 1 && episodesList.get(0).getFewEpisodes().contains("剧场")) {
                    anime.setIsEnd("剧场版");
                } else {
                    anime.setIsEnd("共" + count + "集");
                }
            } else {
                anime.setIsEnd("更新到" + count + "集");
            }
        }
        return animeList;
    }

    /**
     * @return java.util.List<cn.yumietv.entity.Anime>
     * @Author yumie
     * @Description //TODO 根据key查询内容已经缓存,没有则返回null
     * @Date 19:11 2019/1/16
     * @Param [key]
     **/

    List<Anime> redisIsExist(String key) {
        //查询缓存中是否存在
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) {
            return null;
        }
        List<Anime> animeList = FastJsonUtil.json2List(o.toString(), Anime.class);
        return animeList;
    }

    List<AnimeEpisodes> paixujuji(List<AnimeEpisodes> episodesList) {
        //创建存放排序过的AnimeEpisodes
        List<AnimeEpisodes> chuliguode = new ArrayList<>();
        //创建存放不包含第x集的
        Map<Integer, AnimeEpisodes> teshude = new HashMap<>();
        for (int i = 0; i < episodesList.size(); i++) {
            //是第n集
            if (episodesList.get(i).getFewEpisodes().contains("第") && !episodesList.get(i).getFewEpisodes().contains(".") && !episodesList.get(i).getFewEpisodes().contains("OV")) {
                //获得当前集数
                String replace = episodesList.get(i).getFewEpisodes().replace("第", "").replace("集", "");
                //转换为int类型
                Integer integer = Integer.valueOf(replace);
                //如果排序集合为空
                if (chuliguode.size() <= 0) {
                    //直接存入排序集合
                    chuliguode.add(episodesList.get(i));
                }
                //排序集合不为空
                else {
                    //循环排序集合
                    for (int j = 0; j < chuliguode.size(); j++) {
                        //转换成int类型
                        String xxxStr = chuliguode.get(j).getFewEpisodes().replace("第", "").replace("集", "");
                        Integer xxx = Integer.valueOf(xxxStr);
                        //如果当前集数大于遍历的集数
                        if (integer > xxx) {
                            //存到遍历的集数前面
                            episodesList.get(i).setFewEpisodes("第" + integer + "集");
                            chuliguode.add(j, episodesList.get(i));
                            //跳出循环
                            break;
                        }
                        //小于遍历的集数
                        else {
                            //如果循环到了最后一个
                            if (j == chuliguode.size() - 1) {
                                //如果当前集数大于最后一个集数
                                if (integer > Integer.valueOf(chuliguode.get(chuliguode.size() - 1).getFewEpisodes().replace("第", "").replace("集", ""))) {
                                    //存到前面
                                    episodesList.get(i).setFewEpisodes("第" + integer + "集");
                                    chuliguode.add(chuliguode.size() - 1, episodesList.get(i));
                                    break;
                                } else {
                                    //存到后面
                                    episodesList.get(i).setFewEpisodes("第" + integer + "集");
                                    chuliguode.add(chuliguode.size(), episodesList.get(i));
                                    break;
                                }
                            }
                            //跳出本次循环
                            continue;
                        }
                    }
                }
            }
            //必须是else if,否则当这个条件不满足时会进入else
            else if (episodesList.get(i).getFewEpisodes().contains("总")) {
                teshude.put(2, episodesList.get(i));
            }
            //不是第n集
            else {
                //存入特殊的map里面,key为出现的索引,value为该对象
                teshude.put(i, episodesList.get(i));
            }
        }
        //遍历特殊map,根据key值存入排序的集合中
        for (Map.Entry<Integer, AnimeEpisodes> m : teshude.entrySet()) {
            chuliguode.add(m.getKey(), m.getValue());
        }
        return chuliguode;
    }

    /**
     * @Author yumie
     * @Description //TODO 获取图片地址
     * @Date 20:30 2019/2/17
     * @Param [id]
     * @return java.lang.String
     **/

    public String getImgUrl(Long id) {
        Anime anime = animeMapper.selectOne(new QueryWrapper<Anime>().eq("id", id));
        return anime.getImgUrl();
    }

}
