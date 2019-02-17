package cn.yumietv.web.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: yumie
 * @Date: 2019/1/29 11:14
 * @Description:
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token") && StringUtils.isNotBlank(cookie.getValue())) {
                    Object o = redisTemplate.opsForValue().get(URLDecoder.decode(cookie.getValue(), "utf8"));
                    if (o != null) {
                        return true;
                    }
                }
            }
        }

        request.getRequestDispatcher(request.getContextPath() + "/login").forward(request, response);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
