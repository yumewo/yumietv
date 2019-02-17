package cn.yumietv.web.interceptor;

import cn.yumietv.entity.User;
import cn.yumietv.utils.FastJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class RefreshTokenHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String token = "";
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = URLDecoder.decode(cookie.getValue(), "utf-8");
                }
            }
            if (StringUtils.isNotBlank(token)) {
                String str = (String) redisTemplate.opsForValue().get(token);
                if (StringUtils.isNotBlank(str)) {
                    //重置token时间
                    redisTemplate.opsForValue().set(token, str, SESSION_EXPIRE, TimeUnit.SECONDS);
                    //用户已登录,将用户信息存入域中
                    User user = FastJsonUtil.json2Bean(str, User.class);
                    request.setAttribute("user", user);
                }
            }
        }

//        System.out.println("我拦截了``````````````------------");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
