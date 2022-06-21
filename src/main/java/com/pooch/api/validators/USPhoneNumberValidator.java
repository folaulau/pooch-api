package com.pooch.api.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.pooch.api.utils.ValidationUtils;

public class USPhoneNumberValidator implements ConstraintValidator<USPhoneNumber, Long> {

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		return ValidationUtils.isPhoneNumberValid(value);
	}

}
