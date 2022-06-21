package com.pooch.api.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = USPhoneNumberValidator.class)
public @interface USPhoneNumber {

	String message() default "not a valid phoneNumber.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
