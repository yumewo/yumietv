package cn.yumietv;

import cn.yumietv.domain.PlayHistory;
import cn.yumietv.domain.SearchAnime;
import cn.yumietv.entity.Anime;
import cn.yumietv.entity.AnimeCategory;
import cn.yumietv.entity.AnimeEpisodes;
import cn.yumietv.mapper.AnimeCategoryMapper;
import cn.yumietv.mapper.AnimeEpisodesMapper;
import cn.yumietv.mapper.AnimeMapper;
import cn.yumietv.mapper.SearchAnimeMapper;
//import cn.yumietv.utils.PinYinUtil;
import cn.yumietv.web.util.SendMailBySSL;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.text.resources.FormatData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YumietvWebApplicationTests {

    @Autowired
    AnimeEpisodesMapper animeEpisodesMapper;

    @Test
    public void contextLoads() {
        List<AnimeEpisodes> episodes = animeEpisodesMapper.selectList(new QueryWrapper<AnimeEpisodes>());
        for (AnimeEpisodes episode : episodes) {
            String[] split = episode.getFileName().split("第");
            String fewEpisodes = "";
            if (split.length == 2) {
                fewEpisodes = "第" + split[1];
            }
            if (split.length == 3) {
                fewEpisodes = "第" + split[2];
            }
            if (episode.getFileName().contains("总集篇")) {
                fewEpisodes = "总集篇";
            }
            if (episode.getFileName().contains("特别")) {
                fewEpisodes = "特别篇";
            }
            if (episode.getFileName().contains("剧场")) {
                fewEpisodes = "剧场版";
            }
            AnimeEpisodes animeEpisodes = new AnimeEpisodes();
            animeEpisodes.setFewEpisodes(fewEpisodes);
            QueryWrapper<AnimeEpisodes> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_name", episode.getFileName());
            //不能根据ID更新,因为id都是相同的
//            episodesExample.createCriteria().andEidEqualTo(episode.getEid());
            animeEpisodesMapper.update(animeEpisodes, queryWrapper);
        }
    }

    @Autowired
    AnimeMapper animeMapper;

    @Test
    public void mybatisPlus() {
//        AnimeExample example = new AnimeExample();
//        example.createCriteria().andIsEndLike("%全%");
//        List<Anime> animeList = ;
//        System.out.println(animeList.size());
        IPage<Anime> animeIPage = animeMapper.selectPage(new Page<Anime>(1, 10), new QueryWrapper<Anime>().like("country", "大陆"));
        List<Anime> animeList = animeIPage.getRecords();
        for (Anime anime1 : animeList) {
            System.out.println(anime1.getTitle() + "------" + anime1.getIsEnd());
        }
    }

    @Test
    public void pinyin4jTest() {
//        String title = "";
//        List<Anime> animeList = animeMapper.selectList(new QueryWrapper<>());
//        for (Anime anime : animeList) {
//            if (StringUtils.isNotBlank(anime.getFirstWord())) {
//                continue;
//            }
//            title = anime.getTitle();
//            if (anime.getTitle().contains("剧场")) {
//                String[] s = anime.getTitle().split(" ");
//                title = s[0].contains("剧场") ? s[1] : s[0];
//            }
//            String firstSpell = PinYinUtil.getFirstSpell(title).substring(0,1).toUpperCase();
//            anime.setFirstWord(firstSpell);
//            animeMapper.update(anime, new UpdateWrapper<Anime>().eq("id", anime.getId()));
//        }

    }

    @Autowired
    SearchAnimeMapper searchAnimeMapper;
    @Autowired
    SolrClient solrClient;

    @Test
    public void solrTest() throws Exception {
        List<SearchAnime> animeList = searchAnimeMapper.getAnimeList();
        Map<Long, String> catesMap = zhuanyiCategory();
        for (SearchAnime searchAnime : animeList) {
            String isEnd = searchAnime.getIsEnd();
            String status = searchAnime.getStatus();
            if (status == "0") {
                continue;
            }
            if (isEnd.contains("全")) {
                isEnd = "已完结";
                status = "共" + status + "集";
            }
            if (isEnd.contains("更")) {
                isEnd = "连载中";
                status = "更新到" + status + "集";
            }
            searchAnime.setIsEnd(isEnd);
            searchAnime.setStatus(status);
            searchAnime.setCategory(catesMap.get(searchAnime.getId()));
            solrClient.addBean(searchAnime);
            solrClient.commit();
        }
    }

    @Autowired
    AnimeCategoryMapper animeCategoryMapper;

    public Map<Long, String> zhuanyiCategory() {
        List<Long> idList = new ArrayList<>();
        Map<Long, String> catesMap = new HashMap<>();
        List<Anime> animeList = animeMapper.selectList(new QueryWrapper<Anime>());
        for (Anime anime : animeList) {
            idList.add(anime.getId());
        }
        int i = 0;
        for (Long aLong : idList) {
            String cates = "";
            List<AnimeCategory> categoryList = animeCategoryMapper.selectList(new QueryWrapper<AnimeCategory>().eq("cid", aLong));
            for (AnimeCategory animeCategory : categoryList) {
                cates = cates + " " + animeCategory.getCategory();
            }
            catesMap.put(aLong, cates);
        }
        return catesMap;
    }

    @Test
    public void solrQuery() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        query.set("df", "*");
        query.setQuery("*");
        query.setFilterQueries("anime_actors:*花泽香菜*");
        QueryResponse response = solrClient.query(query);
        SolrDocumentList solrDocumentList = response.getResults();
        System.out.println("查询结果的总记录数：" + solrDocumentList.getNumFound());
        // 第六步：遍历结果并打印。
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("anime_title"));
            System.out.println(solrDocument.get("anime_actors"));
            System.out.println(solrDocument.get("anime_category_name"));
            System.out.println("----------------------------------------------------------------------------");
        }
    }

    @Test
    public void deleteSolr() throws Exception {
        solrClient.deleteByQuery("*:*");
        solrClient.commit();
    }

    @Test
    public void solrBean() throws IOException, SolrServerException {
        //播放链接
        List<AnimeEpisodes> episodes = animeEpisodesMapper.selectList(new QueryWrapper<AnimeEpisodes>().eq("eid", 15466703921630L));
//        Iterator<AnimeEpisodes> it = episodes.iterator();
//        while (it.hasNext()) {
//            it.remove();
//        }
//        List<Integer> arrayList = new ArrayList<>();
        System.out.println(episodes.size());
        List<AnimeEpisodes> chuliguode = new ArrayList<>();
        AnimeEpisodes teshuep = null;
        int teshu = -1;
        for (int i = 0; i < episodes.size(); i++) {
            if (episodes.get(i).getFewEpisodes().contains("第")) {
                String replace = episodes.get(i).getFewEpisodes().replace("第", "").replace("集", "");
                Integer integer = Integer.valueOf(replace);
                if (chuliguode.size() <= 0) {
                    episodes.get(i).setFewEpisodes("第" + integer + "集");
                    chuliguode.add(episodes.get(i));
                } else {
                    for (int j = 0; j < chuliguode.size(); j++) {
                        String xxxStr = chuliguode.get(j).getFewEpisodes().replace("第", "").replace("集", "");
                        Integer xxx = Integer.valueOf(xxxStr);
                        if (integer > xxx) {
                            episodes.get(i).setFewEpisodes("第" + integer + "集");
                            chuliguode.add(j, episodes.get(i));
                            break;
                        } else {
                            if (j == chuliguode.size() - 1) {
                                if (integer > Integer.valueOf(chuliguode.get(chuliguode.size() - 1).getFewEpisodes().replace("第", "").replace("集", ""))) {
                                    episodes.get(i).setFewEpisodes("第" + integer + "集");
                                    chuliguode.add(chuliguode.size() - 1, episodes.get(i));
                                    break;
                                } else {
                                    episodes.get(i).setFewEpisodes("第" + integer + "集");
                                    chuliguode.add(chuliguode.size(), episodes.get(i));
                                    break;
                                }
                            }
                            continue;
                        }
                    }
                }
            } else if (episodes.get(i).getFewEpisodes().contains("总")) {
                teshu = 2;
                teshuep = episodes.get(i);
            } else {
                teshu = i;
                teshuep = episodes.get(i);
            }
        }
        if (teshuep != null && teshu != -1) {
            chuliguode.add(teshu, teshuep);
        }
        System.out.println(chuliguode.size());
        for (AnimeEpisodes episode : chuliguode) {
            System.out.println(episode.getFewEpisodes());
        }
    }

    @Test
    public void slorQueryByHighlight() throws Exception {
        SendMailBySSL.sendMail("1084460097@qq.com", "123456", "zhmm");
    }

}


