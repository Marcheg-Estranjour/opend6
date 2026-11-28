package fr.opend6;

import org.springframework.boot.SpringApplication;

public class TestOpenD6Application {

	public static void main(String[] args) {
		SpringApplication.from(OpenD6Application::main).with(TestcontainersConfiguration.class).run(args);	}

}
