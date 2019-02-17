package cn.yumietv.web.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class MyApplicationListener implements ServletContextListener {
    private Logger logger = LoggerFactory.getLogger(MyApplicationListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.err.println("MyApplicationListener初始化成功");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }
}

