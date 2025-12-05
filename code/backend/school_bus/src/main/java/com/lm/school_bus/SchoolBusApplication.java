package com.lm.school_bus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lm.school_bus.mapper")
public class SchoolBusApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolBusApplication.class, args);
	}

}
