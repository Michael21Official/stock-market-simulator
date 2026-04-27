package com.remitly.stockmarket.service;

import com.remitly.stockmarket.event.TradeCompletedEvent;
import com.remitly.stockmarket.exception.InsufficientStockInBankException;
import com.remitly.stockmarket.exception.InsufficientStockInWalletException;
import com.remitly.stockmarket.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final BankService bankService;
    private final WalletService walletService;
    private final ApplicationEventPublisher eventPublisher;

    @Retryable(retryFor = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void buyStock(String walletId, String stockName) {
        log.info("Processing BUY: wallet={}, stock={}", walletId, stockName);

        try {
            if (!bankService.stockExists(stockName)) {
                throw new StockNotFoundException(stockName);
            }

            int bankQuantity = bankService.getStockQuantity(stockName);
            log.debug("Bank quantity for {}: {}", stockName, bankQuantity);

            if (bankQuantity < 1) {
                throw new InsufficientStockInBankException(stockName, 1, bankQuantity);
            }

            bankService.decreaseStock(stockName, 1);

            walletService.increaseStock(walletId, stockName, 1);

            eventPublisher.publishEvent(new TradeCompletedEvent("buy", walletId, stockName));

            log.info("BUY completed: wallet={}, stock={}", walletId, stockName);
        } catch (Exception e) {
            log.error("Error in buyStock: ", e);
            throw e;
        }
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void sellStock(String walletId, String stockName) {
        log.info("Processing SELL: wallet={}, stock={}", walletId, stockName);

        if (!bankService.stockExists(stockName)) {
            throw new StockNotFoundException(stockName);
        }

        int walletQuantity = walletService.getStockQuantity(walletId, stockName);
        if (walletQuantity < 1) {
            throw new InsufficientStockInWalletException(walletId, stockName, 1, walletQuantity);
        }

        walletService.decreaseStock(walletId, stockName, 1);

        bankService.increaseStock(stockName, 1);

        eventPublisher.publishEvent(new TradeCompletedEvent("sell", walletId, stockName));

        log.info("SELL completed: wallet={}, stock={}", walletId, stockName);
    }
}