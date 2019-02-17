package cn.yumietv.web.controller;

import cn.yumie.service.SearchService;
import cn.yumietv.domain.SearchAnime;
import cn.yumietv.domain.SearchCondition;
import cn.yumietv.domain.ShaiXuanCondition;
import cn.yumietv.domain.ShaiXuanTiaoJian;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

/**
 * @Auther: yumie
 * @Date: 2019/1/19 13:18
 * @Description:
 */
@Controller
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/shaixuan")
    public String shaixuan(ShaiXuanCondition shaiXuanCondition, Model model) throws Exception {
        //筛选的各个条件
        ShaiXuanTiaoJian tiaojian = searchService.getTiaoJian();
        model.addAttribute("tiaojian", tiaojian);
        //根据各种条件进行筛选
        shaiXuanCondition = searchService.shaixuan(shaiXuanCondition);
        List<SearchAnime> searchAnimeList = shaiXuanCondition.getSearchAnimeList();
        if (searchAnimeList != null && searchAnimeList.size() > 0) {
            model.addAttribute("searchAnimeList", searchAnimeList);
            shaiXuanCondition.setSearchAnimeList(null);
        }
        //返回分页信息和查询信息进行选中
        model.addAttribute("shaiXuanCondition", shaiXuanCondition);
        return "shaixuan";
    }

    @GetMapping("/search")
    public String search(SearchCondition searchCondition, Model model) throws Exception {
        searchCondition = searchService.search(searchCondition);
        List<SearchAnime> searchAnimeList = searchCondition.getSearchAnimeList();
        if (searchAnimeList != null && searchAnimeList.size() > 0) {
            model.addAttribute("searchAnimeList", searchAnimeList);
        }
        model.addAttribute("searchCondition", searchCondition);
        return "search";
    }
}
