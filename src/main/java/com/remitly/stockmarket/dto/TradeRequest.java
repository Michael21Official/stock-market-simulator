package com.remitly.stockmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TradeRequest(
                @NotBlank(message = "Type is required") @Pattern(regexp = "buy|sell", message = "Type must be 'buy' or 'sell'") String type) {
}