package org.knzoon.painthelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PainthelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(PainthelperApplication.class, args);
	}

}
