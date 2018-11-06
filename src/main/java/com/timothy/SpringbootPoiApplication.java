package com.timothy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.timothy.mapper")
public class SpringbootPoiApplication {

	public static void main(String[] args) {
		//我是fix他
		//我是20181106
		//我需要合成到fix上
		SpringApplication.run(SpringbootPoiApplication.class, args);
	}
}
