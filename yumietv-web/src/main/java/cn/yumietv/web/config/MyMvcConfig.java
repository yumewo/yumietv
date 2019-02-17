package cn.yumietv.web.config;

import cn.yumietv.web.interceptor.LoginHandlerInterceptor;
import cn.yumietv.web.interceptor.RefreshTokenHandlerInterceptor;
import cn.yumietv.web.interceptor.Register1Ip1UserHandlerInterceptor;
import cn.yumietv.web.interceptor.WeiGuiHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//通过继承WebMvcConfigurationSupport来添加组件
//如果加了@EnableWebMvc,springboot自动配置不会生效,全面接管配置
//@EnableWebMvc
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        //重定向避免表单重复提交
//        registry.addViewController("/chenggong").setViewName("registerSuccess");
//    }

    /**
     * @return cn.yumietv.interceptor.RefreshTokenHandlerInterceptor
     * @Author yumie
     * @Description //TODO 拦截器加载的时间点在springcontext之前，所以在拦截器中注入自然为null
     * @Date 19:27 2019/1/16
     * @Param []
     **/


    @Bean
    public WeiGuiHandlerInterceptor weiGuiHandlerInterceptor() {
        return new WeiGuiHandlerInterceptor();
    }

    @Bean
    public LoginHandlerInterceptor loginHandlerInterceptor() {
        return new LoginHandlerInterceptor();
    }


    @Bean
    public RefreshTokenHandlerInterceptor refreshTokenHandlerInterceptor() {
        return new RefreshTokenHandlerInterceptor();
    }

    @Bean
    public Register1Ip1UserHandlerInterceptor register1Ip1UserHandlerInterceptor() {
        return new Register1Ip1UserHandlerInterceptor();
    }

    //所有的WebMvcConfigurer组件都会起作用
    @Bean   //将组件注册在容器
    public WebMvcConfigurer webMvcConfigurer() {
        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(weiGuiHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**", "/login", "/user/login");
                registry.addInterceptor(loginHandlerInterceptor()).
                        addPathPatterns("/playHistory", "/playHistory/*");
                registry.addInterceptor(refreshTokenHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
                registry.addInterceptor(register1Ip1UserHandlerInterceptor()).addPathPatterns("/register");
            }

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("start");
                //重定向避免表单重复提交
                registry.addViewController("/rSuccess.html").setViewName("registerSuccess");
            }
        };
        return webMvcConfigurer;
    }
}