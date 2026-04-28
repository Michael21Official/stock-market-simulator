package com.remitly.stockmarket.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TradeRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassForValidBuy() {
        TradeRequest request = new TradeRequest("buy");
        Set<ConstraintViolation<TradeRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassForValidSell() {
        TradeRequest request = new TradeRequest("sell");
        Set<ConstraintViolation<TradeRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailForInvalidType() {
        TradeRequest request = new TradeRequest("invalid");
        Set<ConstraintViolation<TradeRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("buy", "sell");
    }

    @Test
    void shouldFailForNullType() {
        TradeRequest request = new TradeRequest(null);
        Set<ConstraintViolation<TradeRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }
}