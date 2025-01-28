package com.dream.six;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.dream.six.*")
public class DreamSixApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamSixApplication.class, args);
	}

}
