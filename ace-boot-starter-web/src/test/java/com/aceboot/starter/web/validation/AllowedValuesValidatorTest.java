package com.aceboot.starter.web.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AllowedValuesValidatorTest {

    private AllowedValuesValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AllowedValuesValidator();
        AllowedValues annotation = new AllowedValues() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return AllowedValues.class;
            }

            @Override
            public String message() {
                return "";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String[] value() {
                return new String[] {"hi", "hello"};
            }
        };
        validator.initialize(annotation);
    }

    @Test
    void shouldPassWhenValueAllowed() {
        assertThat(validator.isValid("hi", null)).isTrue();
    }

    @Test
    void shouldFailWhenValueNotAllowed() {
        assertThat(validator.isValid("hey", null)).isFalse();
    }

    @Test
    void shouldAllowNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }
}
