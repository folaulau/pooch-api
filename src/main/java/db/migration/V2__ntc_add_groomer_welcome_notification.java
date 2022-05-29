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
public class V2__ntc_add_groomer_welcome_notification extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    log.info("migrating V1__2__Add_welcome_notification...");
    JdbcTemplate jdbcTemplate =
        new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));


    /**
     * groomer welcome notification
     */
    long notificationId = 1;
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(
        "INSERT INTO notification (id, uuid, email, push_notification, created_at, updated_at)");
    queryBuilder.append("VALUES (" + notificationId
        + ", 'WELCOME_GROOMER', true, true, '2022-05-28 00:37:08.738098', '2022-05-28 00:37:08.738098')");

    try {
      String query = queryBuilder.toString();
      log.info("insert new notificaiton - query : {}", query.toString());
      jdbcTemplate.update(query, new Object[] {});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

    log.debug("notificationId={}", notificationId);


    /**
     * groomer welcome email
     */


    long emailTemplateId = 1;
    queryBuilder = new StringBuilder();
    queryBuilder.append("INSERT INTO email_template ");
    queryBuilder.append(
        "(id, uuid, content, subject, created_by_user, last_updated_by_user, deleted, notification_id, created_at, updated_at) ");
    queryBuilder.append("VALUES ");
    queryBuilder.append("(" + emailTemplateId
        + ", 'WELCOME_GROOMER_EMAIL', '<div>Welcome to Poochfolio. We are here for you an all that you need to manage your bookings.<div/>', 'Welcome To Poochfolio', 'system', 'system', false, ");
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

    /**
     * parent welcome email
     */

    /**
     * parent welcome notification
     */



    // StringBuilder query = new StringBuilder();
    // query.append("INSERT INTO users (first_name) VALUES ( ? )");
    //
    // try {
    // jdbcTemplate.update(query.toString(), new Object[]{"Folau"});
    // } catch (Exception e) {
    // log.warn("Exception, msg={}", e.getLocalizedMessage());
    // }

    log.info("done migrating V1__2__Add_welcome_notification!");
  }

}
