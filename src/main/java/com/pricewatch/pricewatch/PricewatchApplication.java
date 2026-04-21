package com.pricewatch.pricewatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PricewatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricewatchApplication.class, args);
	}

}
