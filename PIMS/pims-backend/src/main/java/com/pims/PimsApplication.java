package com.pims;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * PIMS 启动类
 * 
 * @author PIMS
 */
@SpringBootApplication
@MapperScan("com.pims.mapper")
@EnableScheduling
public class PimsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PimsApplication.class, args);
        System.out.println("========================================");
        System.out.println("PIMS 后端系统启动成功！");
        System.out.println("接口文档地址：http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }
}
