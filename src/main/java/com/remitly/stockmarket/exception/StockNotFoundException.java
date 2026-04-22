package com.remitly.stockmarket.exception;

public class StockNotFoundException extends RuntimeException {

    private final String stockName;

    public StockNotFoundException(String stockName) {
        super("Stock '" + stockName + "' does not exist");
        this.stockName = stockName;
    }

    public String getStockName() {
        return stockName;
    }
}