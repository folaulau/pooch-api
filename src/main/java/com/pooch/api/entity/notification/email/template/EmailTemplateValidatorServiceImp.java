package com.pooch.api.entity.notification.email.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailTemplateValidatorServiceImp implements EmailTemplateValidatorService {

  @Autowired
  private EmailTemplateDAO emailTemplateDAO;

  // @Override
  // public void validateCreate(EmailTemplateCreateDTO templateCreateDTO) {
  // EmailTemplateUuid uuid = templateCreateDTO.getUuid();
  //
  // List<String> subErrors = new ArrayList<>();
  //
  // if (uuid == null) {
  // subErrors.add("uuid is required");
  // }
  //
  // String content = templateCreateDTO.getContent();
  //
  // if (content == null || content.trim().length() <= 0) {
  // subErrors.add("content is required");
  // } else if (content.trim().length() > 10000) {
  // subErrors.add("content size is over the limit of 10000 characters");
  // }
  //
  // if (subErrors.size() > 0) {
  // throw new ApiException(subErrors);
  // }
  // }
  //
  // @Override
  // public EmailTemplate validateUpdate(EmailTemplateUpdateDTO templateUpdateDTO) {
  // EmailTemplateUuid uuid = templateUpdateDTO.getUuid();
  //
  // List<String> subErrors = new ArrayList<>();
  //
  // if (uuid == null) {
  // subErrors.add("uuid is required");
  // }
  //
  // String content = templateUpdateDTO.getContent();
  //
  // if (content == null || content.trim().length() <= 0) {
  // subErrors.add("content is required");
  // } else if (content.trim().length() > 10000) {
  // subErrors.add("content size is over the limit of 10000 characters");
  // }
  //
  // Optional<EmailTemplate> optEmailTemplate =
  // emailTemplateDAO.getByUuid(templateUpdateDTO.getUuid());
  //
  // if (!optEmailTemplate.isPresent()) {
  // subErrors.add("uuid not found");
  // }
  //
  // if (subErrors.size() > 0) {
  // throw new ApiException(subErrors);
  // }
  //
  // EmailTemplate dbEmailTemplate = optEmailTemplate.get();
  //
  // return dbEmailTemplate;
  // }

}
