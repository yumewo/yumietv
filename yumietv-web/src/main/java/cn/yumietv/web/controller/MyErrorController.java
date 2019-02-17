package cn.yumietv.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: yumie
 * @Date: 2019/1/29 10:48
 * @Description:
 */
@Controller
public class MyErrorController implements ErrorController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/error")
    public String handlerError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 404) {
            return "redirect:/index";
        } else {
            return "error";
        }
    }

    @Override
    public String getErrorPath() {
        return "error";
    }
}
