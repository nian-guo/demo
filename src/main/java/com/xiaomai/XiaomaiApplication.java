package com.xiaomai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("com.xiaomai.dao.mapper")
public class XiaomaiApplication extends SpringBootServletInitializer {

    // 主方法 —— 正常运行
    public static void main(String[] args) {
        SpringApplication.run(XiaomaiApplication.class, args);
    }

    // Tomcat 部署必须加这个方法
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(XiaomaiApplication.class);
    }
}