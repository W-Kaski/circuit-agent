package com.eric.ekaiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class
})
public class EkAiAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(EkAiAgentApplication.class, args);
	}

}

