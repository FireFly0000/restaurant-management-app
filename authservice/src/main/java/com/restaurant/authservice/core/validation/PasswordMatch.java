package com.restaurant.authservice.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
    String message() default "{auth.signup.password.not_match}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String password();
    String confirmPassword();
}
