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
public class V4__ntc_add_groomer_new_booking_notification extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    log.info("migrating V4__ntc_add_groomer_new_booking_notification...");
    JdbcTemplate jdbcTemplate =
        new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));


    /**
     * groomer welcome notification
     */
    long notificationId = 3;
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(
        "INSERT INTO notification (id, uuid, description, email, push_notification, created_at, updated_at)");
    queryBuilder.append("VALUES (" + notificationId
        + ", 'SEND_NEW_BOOKING_DETAILS', 'sending booking details to the groomer and the parent', true, true, NOW(), NOW())");

    try {
      String query = queryBuilder.toString();
      log.info("insert new notificaiton - query : {}", query.toString());
      jdbcTemplate.update(query, new Object[] {});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

    log.debug("notificationId={}", notificationId);


    long emailTemplateId = 3;
    queryBuilder = new StringBuilder();
    queryBuilder.append("INSERT INTO email_template ");
    queryBuilder.append(
        "(id, uuid, content, subject, send_to_user, created_by_user, last_updated_by_user, deleted, notification_id, created_at, updated_at) ");
    queryBuilder.append("VALUES ");
    queryBuilder.append("(" + emailTemplateId
        + ", 'GROOMER_NEW_BOOKING_EMAIL', '<div>You have a new booking.<div/>', 'New Booking', 'Groomer', 'system', 'system', false, ");
    queryBuilder.append(notificationId);
    queryBuilder.append(", NOW(), NOW())");

    try {
      String query = queryBuilder.toString();
      log.info("insert new email_template - query : {}", query.toString());
      jdbcTemplate.update(query, new Object[] {});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

    log.debug("emailTemplateId={}", emailTemplateId);

     emailTemplateId = 4;
    queryBuilder = new StringBuilder();
    queryBuilder.append("INSERT INTO email_template ");
    queryBuilder.append(
        "(id, uuid, content, subject, send_to_user, created_by_user, last_updated_by_user, deleted, notification_id, created_at, updated_at) ");
    queryBuilder.append("VALUES ");
    queryBuilder.append("(" + emailTemplateId
        + ", 'PARENT_NEW_BOOKING_EMAIL', '<div>Details of your booking {{details}}.<div/>', 'New Booking Details', 'Parent', 'system', 'system', false, ");
    queryBuilder.append(notificationId);
    queryBuilder.append(", NOW(), NOW())");

    try {
      String query = queryBuilder.toString();
      log.info("insert new email_template - query : {}", query.toString());
      jdbcTemplate.update(query, new Object[] {});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

    log.debug("emailTemplateId={}", emailTemplateId);

    log.info("done migrating V4__ntc_add_groomer_new_booking_notification!");
  }

}
