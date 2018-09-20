package com.timothy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.timothy.mapper")
public class SpringbootPoiApplication {

	public static void main(String[] args) {
	    //master提交下
		SpringApplication.run(SpringbootPoiApplication.class, args);
	}
}
