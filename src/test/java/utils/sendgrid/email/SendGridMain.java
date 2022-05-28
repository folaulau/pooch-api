package utils.sendgrid.email;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;

public class SendGridMain {

  public static void main(String[] args) {

    Email from = new Email("no-reply@poochapp.com");
    String subject = "Sending with SendGrid is Fun";
    Email to = new Email("folaukaveinga+sdfads@gmail.com");
    Content content =
        new Content("text/html", "<h2>Title</h2>and easy to do anywhere, even with Java");
    Mail mail = new Mail(from, subject, to, content);

    Personalization personalization = new Personalization();
    personalization.setSubject("test sub");
    personalization.addTo(to);
    personalization.setFrom(from);
    mail.addPersonalization(personalization);
    String key = "";
    SendGrid sg = new SendGrid(key);
    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);
      System.out.println(response.getStatusCode());
      System.out.println(response.getBody());
      System.out.println(response.getHeaders());
    } catch (IOException ex) {
      System.out.println(ex.getLocalizedMessage());
    }
  }
}
