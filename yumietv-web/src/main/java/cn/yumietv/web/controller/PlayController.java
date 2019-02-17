package cn.yumietv.web.controller;

import cn.yumie.service.AnimeEpisodesService;
import cn.yumie.service.AnimeService;
import cn.yumietv.domain.PlayHistory;
import cn.yumietv.entity.AnimeEpisodes;
import cn.yumietv.entity.User;
import cn.yumietv.utils.FastJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PlayController {
    @Autowired
    private AnimeEpisodesService animeEpisodesService;
    @Autowired
    private AnimeService animeService;
    @Value("${USER_PLAYHISTORY_PRE}")
    private String PLAYHISTORY_PRE;
    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("/play/{fileName}")
    public String play(@PathVariable String fileName, HttpServletRequest request,
                       HttpServletResponse response) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(fileName)) {
            return "redirect:index";
        }
        AnimeEpisodes episodes = animeEpisodesService.getEpisodeByFileName(fileName);
        if (episodes==null) {
            return "redirect:index";
        }
        request.setAttribute("episodes", episodes);
        //判断用户是否登录,登录将观看历史存入redis,没有则存入cookie

        //获取当前天
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).split(" ")[0];
        //把时间切割为年月日和时分秒

        //动漫名
        String animeTitle = episodes.getFileName().replace(episodes.getFewEpisodes(), "");
        //图片路径
        String imgUrl = animeService.getImgUrl(episodes.getEid());
        Object user = request.getAttribute("user");
        if (user != null) {
            //创建观看历史集合
            List<PlayHistory> histories = new ArrayList<>();
            PlayHistory nyrAndMap = new PlayHistory();
            //存入当前日
            nyrAndMap.setNyr(today);
            //存入动漫名集数+图片地址
            nyrAndMap.getSfmAndName().put(fileName, imgUrl);
            User u = (User) user;
            Object historyJson = redisTemplate.opsForValue().get(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail());
            //缓存中没有
            if (historyJson == null) {
                histories.add(nyrAndMap);
                redisTemplate.opsForValue().set(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail(), FastJsonUtil.list2Json(histories));
            } else {
                //缓存中有把histories集合换成缓存中的
                histories = FastJsonUtil.json2List(historyJson.toString(), PlayHistory.class);
                //跳出多重循环
                boolean ok = false;
                //遍历list取出每个PlayHistory对象
                for (int i = 0; i < histories.size(); i++) {
                    //遍历Map,删除存在的此动漫记录,key是动漫名,value是图片地址
                    Iterator<Map.Entry<String, String>> entryIterator = histories.get(i).getSfmAndName().entrySet().iterator();
                    while (entryIterator.hasNext()) {
//                        删除成功,跳出里循环
                        if (ok) {
                            break;
                        }
                        //是否包含该动漫名
                        if (entryIterator.next().getKey().contains(animeTitle)) {
                            entryIterator.remove();
                            ok = true;
                        }
                    }
                    //如果有当天记录,将创建的PlayHistory对象的sfmAndName属性换成换成缓存中的并加入当前记录
                    if (histories.get(i).getNyr().equals(today)) {
                        nyrAndMap.setSfmAndName(histories.get(i).getSfmAndName());
                        nyrAndMap.getSfmAndName().put(fileName, imgUrl);
                        //删除缓存中当天记录
                        histories.remove(i);
                        i--;
                        if (ok) {
                            break;
                        }
                    }
                }
                histories.add(0, nyrAndMap);
                for (int i = 0; i < histories.size(); i++) {
                    PlayHistory playHistory = histories.get(i);
                    if (playHistory.getSfmAndName() == null || playHistory.getSfmAndName().size() <= 0) {
                        histories.remove(i);
                    }

                }
                redisTemplate.opsForValue().set(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail(), FastJsonUtil.list2Json(histories));
            }

        } else {
            //cookie只保存当天,只取时分秒
            Map<String, String> histories = new HashMap<>();
            Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length <= 0) {
                histories.put(fileName, imgUrl);
            } else {
                boolean haveThisCookie = false;
                boolean haveThisjl = false;
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("playHistory") && StringUtils.isNotBlank(cookie.getValue())) {
                        haveThisCookie = true;
                        String json = URLDecoder.decode(cookie.getValue(), "utf-8");
                        histories = FastJsonUtil.json2Map(json);
                        //遍历Map,查看map的值是否有当前动漫
                        Iterator<Map.Entry<String, String>> iterator = histories.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();
                            if (entry.getKey().contains(animeTitle)) {
                                iterator.remove();
                                histories.put(fileName, imgUrl);
                                haveThisjl = true;
                                break;
                            }
                        }
                        break;
                    }

                }
                //没有这个cookie或者cookie的值没有当前时间的key
                if (!haveThisCookie || !haveThisjl) {
                    histories.put(fileName, imgUrl);
                }
            }
            Cookie cookie = new Cookie("playHistory", URLEncoder.encode(FastJsonUtil.list2Json(histories), "utf-8"));
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return "play";
    }

    @GetMapping("/playHistory")
    public String getHistory(HttpServletRequest request) {
        if (request.getAttribute("user") == null) {
            return "login";
        }
        User u = (User) request.getAttribute("user");
        Object o = redisTemplate.opsForValue().get(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail());
        if (o != null) {
            List<PlayHistory> history = FastJsonUtil.json2List(o.toString(), Map.class);
            request.setAttribute("history", history);
        }
        return "playhistory";
    }

    @GetMapping("/playHistory/rm")
    public String removerHistory(String title, String nyr, HttpServletRequest request) {
        User u = (User) request.getAttribute("user");
        String s = redisTemplate.opsForValue().get(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail()).toString();
        List<PlayHistory> histories = FastJsonUtil.json2List(s, PlayHistory.class);
        boolean ok = false;
        for (int i = 0; i < histories.size(); i++) {
            if (ok) {
                break;
            }
            if (histories.get(i).getNyr().equals(nyr)) {
                Map<String, String> sfmAndName = histories.get(i).getSfmAndName();
                Iterator<Map.Entry<String, String>> iterator = sfmAndName.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    if (entry.getKey().equals(title)) {
                        iterator.remove();
                        if (sfmAndName.size() <= 0) {
                            histories.remove(i);
                            if (histories.size() <= 0) {
                                redisTemplate.delete(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail());
                                break;
                            }
                        }
                        redisTemplate.opsForValue().set(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail(), FastJsonUtil.list2Json(histories));
                        break;
                    }
                }
            }
        }
        return "redirect:/playHistory";
    }

    @GetMapping("/playHistory/rmAll")
    public String removeAllHistory(HttpServletRequest request) {
        User u = (User) request.getAttribute("user");
        redisTemplate.delete(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail());
        return "redirect:/playHistory";
    }
}
