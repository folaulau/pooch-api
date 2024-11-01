package com.pooch.api.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullValidator implements ConstraintValidator<NotNull, Object> {

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		return value!=null;
	}
}
