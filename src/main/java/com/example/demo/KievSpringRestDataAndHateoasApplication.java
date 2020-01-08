package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;

@SpringBootApplication
public class KievSpringRestDataAndHateoasApplication {
	
	@Bean
	CommandLineRunner init(OrderRepository repository) {

		return args -> {
			repository.save(new Order("grande mocha"));
			repository.save(new Order("venti hazelnut machiatto"));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(KievSpringRestDataAndHateoasApplication.class, args);
	}

}
