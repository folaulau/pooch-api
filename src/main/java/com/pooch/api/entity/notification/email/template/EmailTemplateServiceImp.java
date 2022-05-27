package com.pooch.api.entity.notification.email.template;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.EntityDTOMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailTemplateServiceImp implements EmailTemplateService {

  @Autowired
  private EmailTemplateDAO emailTemplateDAO;

  @Autowired
  private EntityDTOMapper entityMapper;

  @Autowired
  private EmailTemplateValidatorService emailTemplateValidatorService;
  //
  // @Override
  // public EmailTemplateDTO create(EmailTemplateCreateDTO templateCreateDTO) {
  // emailTemplateValidatorService.validateCreate(templateCreateDTO);
  //
  // EmailTemplate emailTemplate =
  // entityMapper.mapEmailTemplateCreateDTOToEmailTemplate(templateCreateDTO);
  // emailTemplate = emailTemplateDAO.save(emailTemplate);
  //
  // return entityMapper.mapEmailTemplateToEmailTemplateDTO(emailTemplate);
  // }
  //
  // @Override
  // public EmailTemplateDTO update(EmailTemplateUpdateDTO templateUpdateDTO) {
  // EmailTemplate dbEmailTemplate =
  // emailTemplateValidatorService.validateUpdate(templateUpdateDTO);
  //
  // EmailTemplate updateEmailTemplate =
  // entityMapper.mapEmailTemplateUpdateDTOToEmailTemplate(templateUpdateDTO);
  //
  // dbEmailTemplate = entityMapper.patchUpdateEmailTemplate(updateEmailTemplate, dbEmailTemplate);
  //
  // dbEmailTemplate = emailTemplateDAO.save(dbEmailTemplate);
  //
  // return entityMapper.mapEmailTemplateToEmailTemplateDTO(dbEmailTemplate);
  // }

}
