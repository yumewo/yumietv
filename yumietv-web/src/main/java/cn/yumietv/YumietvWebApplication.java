package cn.yumietv;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("cn.yumietv.mapper")
@ServletComponentScan(basePackages = "cn.yumietv.web")
public class YumietvWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(YumietvWebApplication.class, args);
    }

}

