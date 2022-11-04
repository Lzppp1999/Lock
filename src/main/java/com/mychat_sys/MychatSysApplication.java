package com.mychat_sys;

import com.mychat_sys.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(value="com.mychat_sys.mapper")
@ComponentScan(basePackages = {"com.mychat_sys"})
public class MychatSysApplication extends SpringBootServletInitializer {

    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MychatSysApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MychatSysApplication.class, args);
    }

}
