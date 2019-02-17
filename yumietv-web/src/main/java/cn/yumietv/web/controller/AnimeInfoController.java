package cn.yumietv.web.controller;

import cn.yumie.service.AnimeCategoryService;
import cn.yumie.service.AnimeEpisodesService;
import cn.yumie.service.AnimeService;
import cn.yumietv.domain.AnimeAndEpisodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class AnimeInfoController {
    @Autowired
    private AnimeService animeService;
    @Autowired
    private AnimeCategoryService animeCategoryService;
    @Autowired
    private AnimeEpisodesService animeEpisodesService;

    /**
     * @Author yumie
     * @Description //TODO 根据id获取视频的详情信息
     * @Date 16:57 2019/2/17
     * @Param [id, model]
     * @return java.lang.String
     **/
    @RequestMapping("/info/{id}")
    public String itemInfo(@PathVariable Long id, Model model) {
        //取出对应的详细信息放入域中
        AnimeAndEpisodes animeAndEpisodes = animeService.getAnimeByTitle(id);
        //类型
        List<String> cateList = animeCategoryService.getAnimeCategoryById(animeAndEpisodes.getId());
        animeAndEpisodes.setCategories(cateList);
        //总集数
        Integer episodesCount = animeEpisodesService.getEpisodesCountById(animeAndEpisodes.getId());
        animeAndEpisodes.setEpisodesCount(episodesCount);
        //根据此动漫的分类查找相似的动漫推荐
        List<AnimeAndEpisodes> similar = animeCategoryService.getSimilar(animeAndEpisodes.getId(), cateList);
        model.addAttribute("animeAndEpisodes", animeAndEpisodes);
        model.addAttribute("similar", similar);
        return "info";
    }
}
