package cn.yumietv.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@MapperScan("com.houpu.zy.module.*.mapper*")//这个注解，作用相当于下面的@Bean MapperScannerConfigurer，2者配置1份即可
public class MybatisPlusConfig {

    /**
     * mybatis-plus分页插件<br>
     * 文档：http://mp.baomidou.com<br>
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 相当于顶部的：
     * {@code @MapperScan("com.houpu.springboot.module.*.mapper*")}
     * 这里可以扩展，比如使用配置文件来配置扫描Mapper的路径
     */
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
//        scannerConfigurer.setBasePackage("com.houpu.zy.module.*.mapper*");
//        return scannerConfigurer;
//    }
}
