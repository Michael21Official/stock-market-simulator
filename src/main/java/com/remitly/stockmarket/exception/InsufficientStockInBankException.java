package com.remitly.stockmarket.exception;

public class InsufficientStockInBankException extends RuntimeException {

    private final String stockName;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;

    public InsufficientStockInBankException(String stockName, Integer requestedQuantity, Integer availableQuantity) {
        super("Insufficient stock in bank. Requested: " + requestedQuantity + ", Available: " + availableQuantity);
        this.stockName = stockName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getStockName() {
        return stockName;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}