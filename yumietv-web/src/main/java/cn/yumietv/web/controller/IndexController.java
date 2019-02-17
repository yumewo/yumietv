package cn.yumietv.web.controller;

import cn.yumie.service.AnimeService;
import cn.yumietv.entity.Anime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


/*
 * 视频首页controller
 */
@Controller
public class IndexController {
    @Autowired
    private AnimeService animeService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Get请求跳转到相应的页面
     *
     * @param //go
     * @return 跳转页面
     * @author yumie
     * @Date 2018年12月15日 下午10:29:04
     * 废弃了,会拦截这三个以外的/xx,并返回error页面
     */
//	@RequestMapping(value="/{go}",method= RequestMethod.GET)
//	public String register(@PathVariable String go,Model model){
//		if(go.equals("login")){
//			return "login";
//		}
//		if(go.equals("register")){
//			return "register";
//		}
//		if (go.equals("index")) {
//			//准备数据,展示首页
//			List<Anime> hotAnime = animeService.getHotAnime();
//			model.addAttribute("hotAnime", hotAnime);
//			List<Anime> endAnime = animeService.getIsEndAnime();
//			model.addAttribute("endAnime", endAnime);
//			List<Anime> newAnime = animeService.getNewAnime();
//			model.addAttribute("newAnime", newAnime);
//			return "yumietv";
//		}
//		return "error";
//	}
    @GetMapping("/index")
    public String index(Model model) {
        //准备数据,展示首页
        List<Anime> hotAnime = animeService.getHotAnime();
        model.addAttribute("hotAnime", hotAnime);
        List<Anime> endAnime = animeService.getIsEndAnime();
        model.addAttribute("endAnime", endAnime);
        List<Anime> newAnime = animeService.getNewAnime();
        model.addAttribute("newAnime", newAnime);
        return "yumietv";
    }


    @GetMapping("/login")
    public String login(HttpServletRequest request) throws UnsupportedEncodingException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token") && StringUtils.isNotBlank(cookie.getValue())) {
                    Object o = redisTemplate.opsForValue().get(URLDecoder.decode(cookie.getValue(), "utf-8"));
                    if (o != null) {
                        return "redirect:/index";
                    }
                }
            }
        }
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/zhmm")
    public String zhmm() {
        return "zhmm";
    }

}
