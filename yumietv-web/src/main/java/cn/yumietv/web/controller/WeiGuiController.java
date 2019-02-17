package cn.yumietv.web.controller;

import cn.yumie.service.WeiGuiService;
import cn.yumietv.entity.User;
import cn.yumietv.web.util.Ipsettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: yumie
 * @Date: 2019/1/30 17:07
 * @Description:
 */
@Controller
public class WeiGuiController {
    @Autowired
    private WeiGuiService weiGuiService;

    @PostMapping("/ip/weigui")
    @ResponseBody
    public void weigui(HttpServletRequest request) {
        String ip = Ipsettings.getIpAddress(request);
        User user = (User) request.getAttribute("user");
        weiGuiService.weigui(ip, user);
    }
}
