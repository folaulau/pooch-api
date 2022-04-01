package com.pooch.api.utils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Utils", description = "Utility Operations")
@Slf4j
@RestController
@RequestMapping("/utils")
public class UtilRestController {

}
