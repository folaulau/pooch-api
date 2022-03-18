package com.pooch.api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class IntegrationTestConfiguration {

	@Autowired
	private HikariDataSource dataSource;

	private static HikariDataSource staticDataSource;

	@BeforeEach
	public void saveDataSource() {
		staticDataSource = dataSource;
	}
	
	@AfterAll
	public static void closePool() {
		if (staticDataSource != null) {
			staticDataSource.close();
		}
	}

}