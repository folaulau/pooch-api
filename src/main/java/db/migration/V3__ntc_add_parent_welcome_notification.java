package db.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class V3__ntc_add_parent_welcome_notification extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    log.info("migrating V1__2__Add_welcome_notification...");
    JdbcTemplate jdbcTemplate =
        new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));

    /**
     * parent welcome notification
     */


    long notificationId = 2;
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(
        "INSERT INTO notification (id, uuid, email, push_notification, created_at, updated_at)");
    queryBuilder.append("VALUES (" + notificationId
        + ", 'WELCOME_PARENT', true, true, '2022-05-28 00:37:08.738098', '2022-05-28 00:37:08.738098')");

    try {
      String query = queryBuilder.toString();
      log.info("insert new notificaiton - query : {}", query.toString());
      jdbcTemplate.update(query, new Object[] {});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

    log.debug("notificationId={}", notificationId);



    /**
     * parent welcome email
     */


    long emailTemplateId = 2;
    queryBuilder = new StringBuilder();
    queryBuilder.append("INSERT INTO email_template ");
    queryBuilder.append(
        "(id, uuid, content, created_by_user, last_updated_by_user, deleted, notification_id, created_at, updated_at) ");
    queryBuilder.append("VALUES ");
    queryBuilder.append("(" + emailTemplateId
        + ", 'WELCOME_PARENT_EMAIL', '<h1>Welcome</>', 'system', 'system', true, ");
    queryBuilder.append(notificationId);
    queryBuilder.append(", '2022-05-28 00:37:08.738098', '2022-05-28 00:37:08.738098')");

    try {
      String query = queryBuilder.toString();
      log.info("insert new email_template - query : {}", query.toString());
      jdbcTemplate.update(query, new Object[] {});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

    log.debug("emailTemplateId={}", emailTemplateId);


    log.info("done migrating V1__2__Add_welcome_notification!");
  }

}
