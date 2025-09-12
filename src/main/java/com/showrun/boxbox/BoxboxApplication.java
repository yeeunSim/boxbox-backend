package com.showrun.boxbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoxboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoxboxApplication.class, args);
	}

}
