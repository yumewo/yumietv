package cn.yumietv.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.yumie.service.AnimeEpisodesService;
import cn.yumie.service.UserService;
import cn.yumietv.domain.PlayHistory;
import cn.yumietv.entity.AnimeEpisodes;
import cn.yumietv.entity.UserIp;
import cn.yumietv.utils.FastJsonUtil;
import cn.yumietv.web.util.Ipsettings;
import cn.yumietv.web.util.SendMailBySSL;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import cn.yumietv.entity.User;
import cn.yumietv.utils.EmailUtils;
import cn.yumietv.utils.YumieResult;

/**
 * 用户管理Controller
 *
 * @author yumie
 * @Date 2018年12月15日 下午10:14:28
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${USERCODE_PRE}")
    private String USERCODE_PRE;
    @Value("${USERCODE_EXPIRE}")
    private Integer USERCODE_EXPIRE;
    @Value("${Cookie_MAXAGE}")
    private Integer Cookie_MAXAGE;
    @Autowired
    private AnimeEpisodesService animeEpisodesService;
    @Value("${USER_PLAYHISTORY_PRE}")
    private String PLAYHISTORY_PRE;

    /**
     * 用户注册表单提交
     *
     * @param user 表单的值封装到User对象中,没有id
     * @return 信息不为空返回注册成功页面, 为空则返回到注册页面
     * @author yumie
     * @Date 2018年12月15日 下午10:31:39
     */
    @PostMapping("/register")
    public String userRegister(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getEmail()) || StringUtils.isBlank(user.getUsername())) {
            request.setAttribute("errorMessage", "错误的提交方式!");
            return "register";
        }
        String ip = Ipsettings.getIpAddress(request);
        YumieResult result = userService.insertUser(user, ip);
        if (result.getStatus() != 200) {
            request.setAttribute("errorMessage", result.getMsg());
            return "register";
        }
        //将key保存到Cookie中
        String token = (String) result.getData();
        //将token用utf-8进行编码,不然会报34错误
        Cookie cookie = new Cookie("token", URLEncoder.encode(token, "utf-8"));
        cookie.setPath("/");
        cookie.setMaxAge(Cookie_MAXAGE);
        response.addCookie(cookie);
        //CookieUtils.setCookie(request, response, "token", token, Cookie_MAXAGE, true);
        return "redirect:/rSuccess.html";
    }

    /**
     * 用户登录
     *
     * @param str 用户名或者邮箱
     * @param password
     * @return  登录成功转到首页,失败转发到login并添加错误信息
     * @author yumie
     * @Date 2018年12月15日 下午10:40:48
     */
    @PostMapping("/login")
    public String login(@RequestParam("str") String str, @RequestParam("password") String password, Model model,
                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(password)) {
            model.addAttribute("errorMessage", "错误的提交方式!");
            return "login";
        }
        String ip = Ipsettings.getIpAddress(request);
        YumieResult result = userService.loginByUsernameOrEmail(str, password, ip);
        if (result.getStatus() != 200) {
            String message = result.getMsg();
            model.addAttribute("message", message);
            return "login";
        }
        String token = (String) result.getData();
        //将token保存入cookie中
        Cookie tokenCookie = new Cookie("token", URLEncoder.encode(token, "utf-8"));
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(Cookie_MAXAGE);
        response.addCookie(tokenCookie);
        //判断cookie中是否有播放记录
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("playHistory") && StringUtils.isNotBlank(cookie.getValue())) {
                    //获取当前年月日
                    String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).split(" ")[0];

                    List<PlayHistory> histories = new ArrayList<>();
                    PlayHistory nyrAndMap = new PlayHistory();
                    //cookie中的播放记录
                    String json = URLDecoder.decode(cookie.getValue(), "utf-8");
                    Map<String, String> sAndName = FastJsonUtil.json2Map(json);

                    nyrAndMap.setNyr(today);
                    nyrAndMap.setSfmAndName(sAndName);

                    User u = FastJsonUtil.json2Bean(redisTemplate.opsForValue().get(token).toString(), User.class);
                    Object historyJson = redisTemplate.opsForValue().get(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail());
                    if (historyJson == null) {
                        histories.add(nyrAndMap);
                    } else {
                        histories = FastJsonUtil.json2List(historyJson.toString(), PlayHistory.class);

                        //遍历cookie中的播放历史
                        Iterator<Map.Entry<String, String>> iteratorCookie = sAndName.entrySet().iterator();
                        while (iteratorCookie.hasNext()) {
                            Map.Entry<String, String> entry = iteratorCookie.next();
                            AnimeEpisodes episode = animeEpisodesService.getEpisodeByFileName(entry.getKey());
                            String animeTitle = episode.getFileName().replace(episode.getFewEpisodes(), "");
                            boolean ok = false;
                            //用户的整个播放历史
                            for (PlayHistory history : histories) {
                                if (ok) {
                                    break;
                                }
                                //每天的播放历史
                                Iterator<Map.Entry<String, String>> entryIterator = history.getSfmAndName().entrySet().iterator();
                                while (entryIterator.hasNext()) {
                                    Map.Entry<String, String> stringEntry = entryIterator.next();
                                    if (stringEntry.getKey().contains(animeTitle)) {
                                        entryIterator.remove();
                                        ok = true;
                                        break;
                                    }
                                }
                            }
                        }
                        //如果出现删完重复记录,当天的记录为空,则会删两次
                        boolean todayNull = false;
                        for (int i = 0; i < histories.size(); i++) {
                            PlayHistory playHistory = histories.get(i);
                            //删除value为空的map
                            if (playHistory.getSfmAndName() == null || playHistory.getSfmAndName().size() <= 0) {
                                if (!playHistory.getNyr().equals(today)) {
                                    histories.remove(i);
                                    i--;
                                }
                            }
                            //判断是否有当天的记录,有则取出当天的记录修改
                            if (playHistory.getNyr().equals(today)) {
                                Map<String, String> beforeSfmAndName = playHistory.getSfmAndName();
                                Iterator<Map.Entry<String, String>> iterator = beforeSfmAndName.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Map.Entry<String, String> next = iterator.next();
                                    nyrAndMap.getSfmAndName().put(next.getKey(), next.getValue());
                                }
                                histories.remove(i);
                                i--;
                            }

                        }
                        histories.add(0, nyrAndMap);
                    }
                    redisTemplate.opsForValue().set(PLAYHISTORY_PRE + u.getUsername() + ":" + u.getEmail(), FastJsonUtil.list2Json(histories));
                    Cookie hiscookie = new Cookie("playHistory", null);
                    hiscookie.setPath("/");
                    hiscookie.setMaxAge(0);
                    response.addCookie(hiscookie);
                }
            }
        }
        return "redirect:/index";
    }

    /**
     * 异步校验用户名是否存在
     *
     * @param username
     * @return YumieResult
     * @Title: usernameIsExist
     * @Description: TODO
     * @author yumie
     * @date 2018年12月16日上午3:24:01
     */
    @GetMapping("/usernameIsExist/{username}")
    @ResponseBody
    public YumieResult usernameIsExist(@PathVariable String username) {
        YumieResult yumieResult = userService.usernameIsExist(username);
        return yumieResult;
    }

    /**
     * 异步校验邮箱是否存在
     *
     * @param email
     * @return YumieResult
     * @Title: usernameIsExist
     * @Description: TODO
     * @author yumie
     * @date 2018年12月16日上午3:24:01
     */
    @GetMapping(value = "/emailIsExist/{email}")
    @ResponseBody
    public YumieResult emailIsExist(@PathVariable String email) {
        YumieResult yumieResult = userService.emailIsExist(email);
        return yumieResult;
    }

    /**
     * @Author yumie
     * @Description //TODO 发送验证码,注册或是找回密码
     * @Date 16:29 2019/2/17
     * @Param [json, session] json串有两条数据,用户的email和用途
     * @return void
     **/

    @PostMapping(value = "/sendCode")
    @ResponseBody
    public void getEmailCode(@RequestBody String json, HttpSession session) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String email = jsonObject.get("email").toString();
        String yt = jsonObject.get("yt").toString();
        if (StringUtils.isNotBlank(email) && EmailUtils.isEmail(email)) {
            String code = String.valueOf(new Random().nextInt(899999) + 100000);
            SendMailBySSL.sendMail(email, code, yt);
            if (yt.equals("register")) {
                //将验证码存入redis中
                redisTemplate.opsForValue().set(USERCODE_PRE + email + ":register", code, USERCODE_EXPIRE, TimeUnit.SECONDS);
            }
            if (yt.equals("zhmm")) {
                //将验证码存入redis中
                redisTemplate.opsForValue().set(USERCODE_PRE + email + ":zhmm", code, USERCODE_EXPIRE, TimeUnit.SECONDS);
            }
            //存入倒计时
            session.setAttribute("email",email);
            redisTemplate.opsForValue().set(USERCODE_PRE + email + ":sendEmailCD", code, 60, TimeUnit.SECONDS);
        }
    }

    /**
     * @Author yumie
     * @Description //TODO 获取发送邮件的倒计时
     * @Date 16:55 2019/2/17
     * @Param [yt, session]
     * @return java.lang.String
     **/

    @GetMapping("/getdjs")
    @ResponseBody
    private String getDjs(String yt, HttpSession session) {
        Object email = session.getAttribute("email");
        if (email == null) {
            return "0";
        } else {
            Long expire = redisTemplate.getExpire(USERCODE_PRE + email + ":sendEmailCD", TimeUnit.SECONDS);
            if (expire != null && expire > 0) {
                return expire.toString();
            } else {
                return "0";
            }
        }
    }

    /**
     * @Author yumie
     * @Description //TODO 表单提交前判断验证码是否正确
     * @Date 16:56 2019/2/17
     * @Param [json]
     * @return cn.yumietv.utils.YumieResult
     **/

    @PostMapping("/codeIsTrue")
    @ResponseBody
    public YumieResult EmailcodeIsTrue(@RequestBody String json) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String email = jsonObject.get("email").toString();
        String code = jsonObject.get("code").toString();
        String yt = jsonObject.get("yt").toString();
        String codes = null;
        if (yt.equals("register")) {
            codes = (String) redisTemplate.opsForValue().get(USERCODE_PRE + email + ":register");
        }
        if (yt.equals("zhmm")) {
            codes = (String) redisTemplate.opsForValue().get(USERCODE_PRE + email + ":zhmm");
        }
        if (StringUtils.isBlank(codes) || !codes.equals(code)) {
            return YumieResult.build(500, "验证码错误或已过期");
        }
        return YumieResult.ok();
    }

    /**
     * @Author yumie
     * @Description //TODO 找回密码
     * @Date 16:56 2019/2/17
     * @Param [user, model]
     * @return java.lang.String
     **/
    @PostMapping("/zhmm")
    public String zhmm(User user, Model model) {
        YumieResult yumieResult = userService.updateUser(user);
        if (yumieResult.getStatus() != 200) {
            model.addAttribute("errorMessage", yumieResult.getMsg());
            return "zhmm";
        }
        return "login";
    }

    /**
     * @Author yumie
     * @Description //TODO 退出登录
     * @Date 16:56 2019/2/17
     * @Param [request, response]
     * @return java.lang.String
     **/

    @GetMapping("/zhuxiao")
    public String zhuxiao(HttpServletRequest request, HttpServletResponse response) {
        Object user = request.getAttribute("user");
        if (user != null) {
            User u = (User) user;
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equals("token")) {
                        redisTemplate.delete(cookies[i].getValue());
                    }

                }
            }
        }
        Cookie hiscookie = new Cookie("token", null);
        hiscookie.setPath("/");
        hiscookie.setMaxAge(0);
        response.addCookie(hiscookie);
        return "redirect:/index";
    }

}
