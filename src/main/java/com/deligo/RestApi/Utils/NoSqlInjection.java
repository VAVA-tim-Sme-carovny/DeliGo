package com.deligo.RestApi.Utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SqlInjectionValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSqlInjection {
    String message() default "Potential SQL injection detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}