package com.pooch.api.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@DependsOn("dataSource")
@Configuration
public class FlywayConfiguration {

  @Autowired
  public FlywayConfiguration(DataSource dataSource) {
    Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate();
    
    // Flyway flyway = Flyway.configure().dataSource(url, user, password).load();
    // flyway.migrate();
  }

  // /** Override default flyway initializer to do nothing */
  // @Bean
  // FlywayMigrationInitializer flywayInitializer() {
  // return new FlywayMigrationInitializer(setUpFlyway(), (f) -> { // do nothing
  // log.info("do no migration yet. wait til hibernate initializes tables...");
  // });
  // }
  //
  // /** Create a second flyway initializer to run after jpa has created the schema */
  // @Bean
  //
  // FlywayMigrationInitializer delayedFlywayInitializer(DataSource dataSource) {
  // Flyway flyway = setUpFlyway();
  //
  // Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate();
  // return new FlywayMigrationInitializer(flyway, null);
  // }

  // private Flyway setUpFlyway() {
  //
  // FluentConfiguration configuration = Flyway.configure().dataSource(databaseUrl,
  // databaseUsername, databasePassword);
  // configuration.schemas(databaseName);
  // configuration.baselineOnMigrate(true);
  // return configuration.load();
  // }
}
