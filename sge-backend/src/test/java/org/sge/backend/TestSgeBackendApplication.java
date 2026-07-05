package org.sge.backend;

import org.springframework.boot.SpringApplication;

public class TestSgeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(SgeBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
