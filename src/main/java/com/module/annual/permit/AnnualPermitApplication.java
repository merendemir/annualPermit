package com.module.annual.permit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnnualPermitApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnualPermitApplication.class, args);
	}

}
