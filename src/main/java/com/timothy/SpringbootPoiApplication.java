package com.timothy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.timothy.mapper")
public class SpringbootPoiApplication {

	public static void main(String[] args) {
	    //master提交下 --i am fixhot i will do Som  这里改下  in 好的 改好了
		SpringApplication.run(SpringbootPoiApplication.class, args);
	}
}
