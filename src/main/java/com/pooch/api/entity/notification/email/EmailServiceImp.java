package com.pooch.api.entity.notification.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImp implements EmailService {

//    @Autowired
//    private AwsEmailService  awsEmailService;

    @Autowired
    private EmailDAO         emailDAO;

    @Value("${project.web.app.uri}")
    private String           appHost;

//    @Autowired
//    private EmailTemplateDAO emailTemplateDAO;

//    @Async
//    @Override
//    public void sendPasswordResetRequest(User user, PasswordReset passwordReset) {
//        /**
//         * Get template
//         */
//
//        Email email = new Email();
//        email.setSendTo(user.getEmail());
//        email.setSubject("Request Password Reset");
//        email.setTemplateUuid(EmailTemplateUuid.REQUEST_PASSWORD_RESET);
//
//        Optional<EmailTemplate> optEmailTemplate = emailTemplateDAO.getByUuid(EmailTemplateUuid.REQUEST_PASSWORD_RESET);
//
//        if (optEmailTemplate.isPresent()) {
//            /**
//             * populate email
//             */
//
//            EmailTemplate emailTemplate = optEmailTemplate.get();
//
//            /**
//             * generate params for html email
//             */
//            Map<String, Object> params = new HashMap<>();
//
//            StringBuilder link = new StringBuilder(appHost);
//            link.append("/password-reset?token=");
//            link.append(passwordReset.getToken());
//
//            params.put("resetPasswordLink", link.toString());
//
//            log.info("params={}", ObjectUtils.toJson(params));
//            log.info("emailTemplate={}", ObjectUtils.toJson(emailTemplate));
//
//            String content = EmailUtils.subValues(params, emailTemplate.getContent());
//
//            email.setContent(content);
//            /**
//             * send email
//             */
//
//            try {
//                email = awsEmailService.send(email);
//
//            } catch (MessagingException e) {
//                log.warn("MessagingException,msg={}", e.getLocalizedMessage());
//                email.setError(e.getMessage());
//            }
//        } else {
//            email.setError("template " + EmailTemplateUuid.REQUEST_PASSWORD_RESET + " not found");
//        }
//
//        /**
//         * save email
//         */
//        emailDAO.save(email);
//    }
//
//    @Async
//    @Override
//    public void sendWelcome(User user) {
//        /**
//         * Get template
//         */
//
//        Email email = new Email();
//        email.setSendTo(user.getEmail());
//        email.setSubject("Welcome to Learn My Math");
//        email.setTemplateUuid(EmailTemplateUuid.WELCOME_USER_UPON_SIGNUP);
//
//        Optional<EmailTemplate> optEmailTemplate = emailTemplateDAO.getByUuid(EmailTemplateUuid.WELCOME_USER_UPON_SIGNUP);
//
//        if (optEmailTemplate.isPresent()) {
//            /**
//             * populate email
//             */
//
//            EmailTemplate emailTemplate = optEmailTemplate.get();
//
//            Map<String, Object> params = new HashMap<>();
//
//            StringBuilder acccountLink = new StringBuilder(appHost);
//            acccountLink.append("/account/profile");
//
//            params.put("accountLink", acccountLink.toString());
//
//            String content = EmailUtils.subValues(params, emailTemplate.getContent());
//
//            email.setContent(content);
//
//            /**
//             * send email
//             */
//
//            try {
//                email = awsEmailService.send(email);
//
//            } catch (MessagingException e) {
//                log.warn("MessagingException,msg={}", e.getLocalizedMessage());
//                email.setError(e.getMessage());
//            }
//            
//        } else {
//            email.setError("template " + EmailTemplateUuid.REQUEST_PASSWORD_RESET + " not found");
//        }
//
//        /**
//         * save email
//         */
//        emailDAO.save(email);
//    }

}
