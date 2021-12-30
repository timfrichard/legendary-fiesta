package com.tim.example.spring.batch.model.constraints;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmptyAllowedMaxMinSizeValidator implements ConstraintValidator<EmptyAllowedMaxMinSize, String> {

    private boolean allowEmpty;

    private String label;

    private int max;

    private int min;

    @Override
    public void initialize(EmptyAllowedMaxMinSize constraintAnnotation) {

        this.allowEmpty = constraintAnnotation.allowEmpty();
        this.label = constraintAnnotation.label();
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String inputValue, ConstraintValidatorContext context) {

        /* Null OR (Within the Min and Max OR Allow Empty and IsEmpty) */
        boolean isValid = inputValue == null || ((inputValue != null && StringUtils.length(inputValue) >= min
                && StringUtils.length(inputValue) <= max) || (inputValue != null &&
                StringUtils.isEmpty(inputValue) && allowEmpty));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            label + " has failed validation using the following text: "
                                    + inputValue)
                    .addConstraintViolation();
        }


        return isValid;
    }
}
