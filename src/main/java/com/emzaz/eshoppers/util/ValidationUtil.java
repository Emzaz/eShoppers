package com.emzaz.eshoppers.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValidationUtil {
    private static final ValidationUtil INSTANCE = new ValidationUtil();

    private final Validator validator;

    private ValidationUtil() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public static ValidationUtil getInstance() {
        return INSTANCE;
    }

    public <T> Map<String, String> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        Map<String, String> errors = new HashMap<>();

        for(ConstraintViolation<T> violation : violations) {
            String path = violation.getPropertyPath().toString();

            if(errors.containsKey(path)) {
                String errorMsg = errors.get(path);
                errors.put(path, errorMsg + " <br/> " + violation.getMessage());
            } else {
                errors.put(path, violation.getMessage());
            }
        }

        return errors;
    }
}
