package db.migration;

import java.sql.PreparedStatement;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class V1__Add_users extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        log.info("migrating V1__1__Add_petsitters...");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));

        // StringBuilder query = new StringBuilder();
        // query.append("INSERT INTO users (first_name) VALUES ( ? )");
        //
        // try {
        // jdbcTemplate.update(query.toString(), new Object[]{"Folau"});
        // } catch (Exception e) {
        // log.warn("Exception, msg={}", e.getLocalizedMessage());
        // }

        log.info("done migrating V1__1__Add_petsitters!");
    }

}
