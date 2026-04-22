package com.remitly.stockmarket.exception;

public class InsufficientStockInWalletException extends RuntimeException {

    private final String walletId;
    private final String stockName;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;

    public InsufficientStockInWalletException(String walletId, String stockName, Integer requestedQuantity,
            Integer availableQuantity) {
        super("Insufficient stock in wallet. Wallet: " + walletId + ", Stock: " + stockName +
                ", Requested: " + requestedQuantity + ", Available: " + availableQuantity);
        this.walletId = walletId;
        this.stockName = stockName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getWalletId() {
        return walletId;
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