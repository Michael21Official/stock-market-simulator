package com.remitly.stockmarket.exception;

public class WalletNotFoundException extends RuntimeException {

    private final String walletId;

    public WalletNotFoundException(String walletId) {
        super("Wallet '" + walletId + "' does not exist");
        this.walletId = walletId;
    }

    public String getWalletId() {
        return walletId;
    }
}