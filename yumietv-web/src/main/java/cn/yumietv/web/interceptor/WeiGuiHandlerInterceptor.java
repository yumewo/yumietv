package cn.yumietv.web.interceptor;

import cn.yumietv.entity.UserIp;
import cn.yumietv.mapper.UserIpMapper;
import cn.yumietv.web.util.Ipsettings;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * @Auther: yumie
 * @Date: 2019/1/30 17:00
 * @Description:
 */
public class WeiGuiHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private UserIpMapper userIpMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = Ipsettings.getIpAddress(request);
        UserIp one = userIpMapper.selectOne(new QueryWrapper<UserIp>().eq("ip", ip));
        if (one != null) {
            if (one.getDengji() == null) {
                return true;
            }
            if (one.getDengji() == 1) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null && cookies.length > 0) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("token")) {
                            Object user = redisTemplate.opsForValue().get(URLDecoder.decode(cookie.getValue(), "utf-8"));
                            if (user != null) {
                                return true;
                            }
                        }
                    }
                }
                request.setAttribute("message", "存在违规操作,必须先登录");
                request.getRequestDispatcher(request.getContextPath() + "/login").forward(request, response);
                return false;
            } else if (one.getDengji() == 2) {
                Object o = redisTemplate.opsForValue().get("xiaoheiwu:" + ip);
                if (o != null) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
