package cn.yumietv.service.impl;

import cn.yumie.service.SearchService;
import cn.yumietv.domain.SearchAnime;
import cn.yumietv.domain.SearchCondition;
import cn.yumietv.domain.ShaiXuanCondition;
import cn.yumietv.domain.ShaiXuanTiaoJian;
import cn.yumietv.mapper.SearchAnimeMapper;
import cn.yumietv.utils.FastJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Auther: yumie
 * @Date: 2019/1/19 13:21
 * @Description:
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    SolrClient solrClient;
    @Autowired
    SearchAnimeMapper searchAnimeMapper;
    @Autowired
    RedisTemplate redisTemplate;

    public SearchCondition search(SearchCondition searchCondition) throws Exception {
        SolrQuery query = new SolrQuery();
        query.set("df", "anime_keywords");
        String gjz = searchCondition.getStr();
        query.setQuery("*" + gjz + "*");
        //分页
        if (searchCondition.getCurrentPage() == null || searchCondition.getCurrentPage() <= 1) {
            query.setStart(0);
        } else {
            //start是从第几个开始,当前页-1*每页显示条数
            query.setStart((searchCondition.getCurrentPage() - 1) * searchCondition.getRows());
        }
        //每页显示条数
        query.setRows(searchCondition.getRows());
        QueryResponse queryResponse = solrClient.query(query);
        //总页数
        Integer numFound = new Long(queryResponse.getResults().getNumFound()).intValue();
        if (numFound == 0) {
            searchCondition.setCurrentPage(0);
            searchCondition.setCountPage(0);
        } else {
            int rows = searchCondition.getRows();
            searchCondition.setCountPage(numFound % rows == 0 ? numFound / rows : (numFound / rows) + 1);
        }
        List<SearchAnime> searchAnimeList = queryResponse.getBeans(SearchAnime.class);
        for (SearchAnime searchAnime : searchAnimeList) {
            if (searchAnime.getTitle().contains(gjz)) {
                searchAnime.setTitle(searchAnime.getTitle().replace(gjz, "<span style='color:red'>" + gjz + "</span>"));
            }
            if (StringUtils.isNotBlank(searchAnime.getActors()) && searchAnime.getActors().contains(gjz)) {
                searchAnime.setActors(searchAnime.getActors().replace(gjz, "<span style='color:red'>" + gjz + "</span>"));
            }
            if (StringUtils.isNotBlank(searchAnime.getDirector()) && searchAnime.getDirector().contains(gjz)) {
                searchAnime.setDirector(searchAnime.getDirector().replace(gjz, "<span style='color:red'>" + gjz + "</span>"));
            }
            searchAnime.setGjz(searchCondition.getStr());
        }
        searchCondition.setSearchAnimeList(searchAnimeList);
        //当前页是否有前三页和后三页
        if (searchCondition.getCurrentPage() - 3 > 0) {
            searchCondition.setQiansanye(searchCondition.getCurrentPage() - 3);
        } else {
            searchCondition.setQiansanye(1);
        }
        if (searchCondition.getCountPage() - searchCondition.getCurrentPage() > 3) {
            searchCondition.setHousanye(searchCondition.getCurrentPage() + 3);
        } else {
            searchCondition.setHousanye(searchCondition.getCountPage());
        }
        return searchCondition;
    }

    public ShaiXuanCondition shaixuan(ShaiXuanCondition shaiXuanCondition) throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        //设置查询条件
        query.set("df", "*");
        query.setQuery("*");
        //设置过滤条件
        //时间
        String sj = shaiXuanCondition.getSj();
        sj = StringUtils.isBlank(sj) ? "*" : sj;
        query.addFilterQuery("anime_createDate:" + sj);

        //地区
        String dq = shaiXuanCondition.getDq();
        dq = StringUtils.isBlank(dq) ? "*" : dq;
        query.addFilterQuery("anime_country:" + dq);

        //状态
        String zt = shaiXuanCondition.getZt();
        zt = StringUtils.isBlank(zt) ? "*" : zt;
        query.addFilterQuery("anime_isEnd:" + zt);

        //类型
        String lx = shaiXuanCondition.getLx();
        lx = StringUtils.isBlank(lx) ? "*" : lx;
        query.addFilterQuery("anime_category:*" + lx + "*");

        //首字母
        String szm = shaiXuanCondition.getSzm();
        szm = StringUtils.isBlank(szm) ? "*" : szm;
        query.addFilterQuery("anime_first_word:" + szm);

        //分页
        if (shaiXuanCondition.getCurrentPage() == null || shaiXuanCondition.getCurrentPage() <= 1) {
            query.setStart(0);
        } else {
            //start是从第几个开始,当前页-1*每页显示条数
            query.setStart((shaiXuanCondition.getCurrentPage() - 1) * shaiXuanCondition.getRows());
        }
        //每页显示条数
        query.setRows(shaiXuanCondition.getRows());
        QueryResponse queryResponse = solrClient.query(query);
        //总页数
        Integer numFound = new Long(queryResponse.getResults().getNumFound()).intValue();
        if (numFound == 0) {
            shaiXuanCondition.setCurrentPage(0);
            shaiXuanCondition.setCountPage(0);
        } else {
            int rows = shaiXuanCondition.getRows();
            shaiXuanCondition.setCountPage(numFound % rows == 0 ? numFound / rows : (numFound / rows) + 1);
        }
        List<SearchAnime> searchAnimeList = queryResponse.getBeans(SearchAnime.class);
        //当前页是否有前三页和后三页
        if (shaiXuanCondition.getCurrentPage() - 3 > 0) {
            shaiXuanCondition.setQiansanye(shaiXuanCondition.getCurrentPage() - 3);
        } else {
            shaiXuanCondition.setQiansanye(1);
        }
        if (shaiXuanCondition.getCountPage() - shaiXuanCondition.getCurrentPage() > 3) {
            shaiXuanCondition.setHousanye(shaiXuanCondition.getCurrentPage() + 3);
        } else {
            shaiXuanCondition.setHousanye(shaiXuanCondition.getCountPage());
        }
        shaiXuanCondition.setSearchAnimeList(searchAnimeList);
        return shaiXuanCondition;
    }


    public ShaiXuanTiaoJian getTiaoJian() {
        ShaiXuanTiaoJian tiaoJian = new ShaiXuanTiaoJian();
        //从缓存中查询
        Object shaixuantiaojian = redisTemplate.opsForValue().get("shaixuantiaojian");
        if (shaixuantiaojian != null) {
            tiaoJian = FastJsonUtil.json2Bean(shaixuantiaojian.toString(), ShaiXuanTiaoJian.class);
            return tiaoJian;
        }

        //获取重复最多的前15个分类
        List<String> category = searchAnimeMapper.selectDuoCategory();
        tiaoJian.setCategories(category);
        List<String> zimu24 = searchAnimeMapper.zimu24();
        tiaoJian.setZimu24(zimu24);
        List<String> nianFen = searchAnimeMapper.getNianFen();
        tiaoJian.setNianfen(nianFen);
        List<String> country = searchAnimeMapper.getCountry();
        tiaoJian.setCountry(country);
        //存入缓存
        redisTemplate.opsForValue().set("shaixuantiaojian", FastJsonUtil.bean2Json(tiaoJian));
        return tiaoJian;
    }

}
