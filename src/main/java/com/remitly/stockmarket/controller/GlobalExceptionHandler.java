package com.remitly.stockmarket.controller;

import com.remitly.stockmarket.exception.InsufficientStockInBankException;
import com.remitly.stockmarket.exception.InsufficientStockInWalletException;
import com.remitly.stockmarket.exception.StockNotFoundException;
import com.remitly.stockmarket.exception.WalletNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockNotFoundException.class)
    public ProblemDetail handleStockNotFound(StockNotFoundException e) {
        log.warn("Stock not found: {}", e.getStockName());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        pd.setTitle("Stock Not Found");
        pd.setType(URI.create("https://api.stockmarket.com/errors/stock-not-found"));
        pd.setProperty("stockName", e.getStockName());
        return pd;
    }

    @ExceptionHandler(InsufficientStockInBankException.class)
    public ProblemDetail handleInsufficientStockInBank(InsufficientStockInBankException e) {
        log.warn("Insufficient stock in bank: {}", e.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        pd.setTitle("Insufficient Stock in Bank");
        pd.setType(URI.create("https://api.stockmarket.com/errors/insufficient-stock-bank"));
        pd.setProperty("stockName", e.getStockName());
        pd.setProperty("requestedQuantity", e.getRequestedQuantity());
        pd.setProperty("availableQuantity", e.getAvailableQuantity());
        return pd;
    }

    @ExceptionHandler(InsufficientStockInWalletException.class)
    public ProblemDetail handleInsufficientStockInWallet(InsufficientStockInWalletException e) {
        log.warn("Insufficient stock in wallet: {}", e.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        pd.setTitle("Insufficient Stock in Wallet");
        pd.setType(URI.create("https://api.stockmarket.com/errors/insufficient-stock-wallet"));
        pd.setProperty("walletId", e.getWalletId());
        pd.setProperty("stockName", e.getStockName());
        pd.setProperty("requestedQuantity", e.getRequestedQuantity());
        pd.setProperty("availableQuantity", e.getAvailableQuantity());
        return pd;
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ProblemDetail handleWalletNotFound(WalletNotFoundException e) {
        log.warn("Wallet not found: {}", e.getWalletId());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        pd.setTitle("Wallet Not Found");
        pd.setType(URI.create("https://api.stockmarket.com/errors/wallet-not-found"));
        pd.setProperty("walletId", e.getWalletId());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setTitle("Validation Error");
        pd.setType(URI.create("https://api.stockmarket.com/errors/validation"));
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException caught: ", e);

        Throwable cause = e.getCause();
        if (cause != null) {
            if (cause instanceof StockNotFoundException) {
                return handleStockNotFound((StockNotFoundException) cause);
            }
            if (cause instanceof InsufficientStockInBankException) {
                return handleInsufficientStockInBank((InsufficientStockInBankException) cause);
            }
            if (cause instanceof InsufficientStockInWalletException) {
                return handleInsufficientStockInWallet((InsufficientStockInWalletException) cause);
            }
            if (cause instanceof WalletNotFoundException) {
                return handleWalletNotFound((WalletNotFoundException) cause);
            }
        }

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        pd.setTitle("Internal Server Error");
        pd.setType(URI.create("https://api.stockmarket.com/errors/internal"));
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception e) {
        log.error("Unexpected error: ", e);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        pd.setTitle("Internal Server Error");
        pd.setType(URI.create("https://api.stockmarket.com/errors/internal"));
        return pd;
    }
}