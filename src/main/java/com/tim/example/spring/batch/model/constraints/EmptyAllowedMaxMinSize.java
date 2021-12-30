package com.tim.example.spring.batch.model.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmptyAllowedMaxMinSizeValidator.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface EmptyAllowedMaxMinSize {

    boolean allowEmpty() default true;

    Class<?>[] groups() default {};

    String label() default "Value ";

    int max() default 0;

    int min() default 0;

    String message() default "Invalid field value";

    Class<? extends Payload>[] payload() default {};

}
