package utils.aws.ses;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.CreateTemplateRequest;
import com.amazonaws.services.simpleemail.model.Template;
import com.pooch.api.config.LocalAwsConfig;
import com.pooch.api.utils.ObjectUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class AwsSESEmail {

  /**
   * https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html
   */

  private static AmazonSimpleEmailService amazonSimpleEmailService;

  private final List<Future<PartETag>> futuresPartETags = new ArrayList<>();

  public static void main(String[] args) {
    LocalAwsConfig awsConfig = new LocalAwsConfig();
//    amazonSimpleEmailService = awsConfig.amazonSES();

    // createTemplate();
  }

  // public static void createTemplate() {
  //
  // CreateTemplateRequest templateReq = new CreateTemplateRequest();
  //
  // Template template = new Template();
  // template.setTemplateName("groomer-welcome-email-dev");
  // template.setSubjectPart("Welcome to Pooch");
  // template.setHtmlPart("<h1>Hello {{name}},</h1><p>Welcome to Pooch!</p>");
  // template.setTextPart("Hello {{name}},\r\nWelcome to Pooch!");
  //
  // templateReq.setTemplate(template);
  //
  // amazonSimpleEmailService.createTemplate(templateReq);
  // }



}
