package cn.yumietv.web.interceptor;

import cn.yumietv.entity.UserIp;
import cn.yumietv.mapper.UserIpMapper;
import cn.yumietv.web.util.Ipsettings;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: yumie
 * @Date: 2019/2/2 11:41
 * @Description:
 */
public class Register1Ip1UserHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private UserIpMapper userIpMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = Ipsettings.getIpAddress(request);
        UserIp userIp = userIpMapper.selectOne(new QueryWrapper<UserIp>().eq("ip", ipAddress));
        if (userIp != null && userIp.getIsRegister() != null && userIp.getIsRegister() == 1) {
            request.setAttribute("errorMessage", "您已注册过账号");
            request.getRequestDispatcher(request.getContextPath() + "/index").forward(request, response);
            return false;
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
