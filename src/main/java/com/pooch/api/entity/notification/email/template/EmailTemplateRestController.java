package com.pooch.api.entity.notification.email.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "EmailTemplates", description = "EmailTemplate Operations")
@Slf4j
@RestController
@RequestMapping("/emails/templates")
public class EmailTemplateRestController {

    @Autowired
    private EmailTemplateService emailTemplateService;

//    @Operation(summary = "Create Template", description = "create template")
//    @PostMapping
//    public ResponseEntity<EmailTemplateDTO> create(@RequestHeader(name = "token", required = true) String token,
//            @Parameter(name = "template", description = "template", required = true) @Valid @RequestBody EmailTemplateCreateDTO template) {
//        log.info("create {}", ObjectUtils.toJson(template));
//
//        EmailTemplateDTO emailTemplateDTO = emailTemplateService.create(template);
//
//        return new ResponseEntity<>(emailTemplateDTO, OK);
//    }

//    @Operation(summary = "Update Template", description = "update template")
//    @PutMapping
//    public ResponseEntity<EmailTemplateDTO> update(@RequestHeader(name = "token", required = true) String token,
//            @Parameter(name = "template", description = "template", required = true) @Valid @RequestBody EmailTemplateUpdateDTO template) {
//        log.info("create {}", ObjectUtils.toJson(template));
//
//        EmailTemplateDTO emailTemplateDTO = emailTemplateService.update(template);
//
//        return new ResponseEntity<>(emailTemplateDTO, OK);
//    }
}
