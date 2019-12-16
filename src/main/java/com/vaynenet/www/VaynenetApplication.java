package com.vaynenet.www;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = "com.vaynenet.www.dao")
@EnableRabbit
public class VaynenetApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaynenetApplication.class, args);
	}

}
