package com.remitly.stockmarket.service;

import com.remitly.stockmarket.event.TradeCompletedEvent;
import com.remitly.stockmarket.exception.InsufficientStockInBankException;
import com.remitly.stockmarket.exception.InsufficientStockInWalletException;
import com.remitly.stockmarket.exception.StockNotFoundException;
import com.remitly.stockmarket.metrics.TradeMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private BankService bankService;

    @Mock
    private WalletService walletService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TradeMetrics tradeMetrics;

    @InjectMocks
    private TradeService tradeService;

    private static final String WALLET_ID = "wallet-123";
    private static final String STOCK_NAME = "AAPL";

    @Test
    void buyStock_ShouldSucceed_WhenBankHasStock() throws Exception {
        when(bankService.stockExists(STOCK_NAME)).thenReturn(true);
        when(bankService.getStockQuantity(STOCK_NAME)).thenReturn(100);
        when(tradeMetrics.recordTradeDuration(any())).thenAnswer(invocation -> {
            java.util.concurrent.Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });

        tradeService.buyStock(WALLET_ID, STOCK_NAME);

        verify(bankService).decreaseStock(STOCK_NAME, 1);
        verify(walletService).increaseStock(WALLET_ID, STOCK_NAME, 1);
        verify(eventPublisher).publishEvent(any(TradeCompletedEvent.class));
        verify(tradeMetrics).recordSuccessfulBuy();
    }

    @Test
    void buyStock_ShouldThrowException_WhenStockDoesNotExist() throws Exception {
        when(bankService.stockExists(STOCK_NAME)).thenReturn(false);
        when(tradeMetrics.recordTradeDuration(any())).thenAnswer(invocation -> {
            java.util.concurrent.Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });

        assertThatThrownBy(() -> tradeService.buyStock(WALLET_ID, STOCK_NAME))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(StockNotFoundException.class);

        verify(tradeMetrics).recordFailedBuy();
    }

    @Test
    void buyStock_ShouldThrowException_WhenBankHasInsufficientStock() throws Exception {
        when(bankService.stockExists(STOCK_NAME)).thenReturn(true);
        when(bankService.getStockQuantity(STOCK_NAME)).thenReturn(0);
        when(tradeMetrics.recordTradeDuration(any())).thenAnswer(invocation -> {
            java.util.concurrent.Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });

        assertThatThrownBy(() -> tradeService.buyStock(WALLET_ID, STOCK_NAME))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(InsufficientStockInBankException.class);

        verify(tradeMetrics).recordFailedBuy();
    }

    @Test
    void sellStock_ShouldSucceed_WhenWalletHasStock() throws Exception {
        when(bankService.stockExists(STOCK_NAME)).thenReturn(true);
        when(walletService.getStockQuantity(WALLET_ID, STOCK_NAME)).thenReturn(5);
        when(tradeMetrics.recordTradeDuration(any())).thenAnswer(invocation -> {
            java.util.concurrent.Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });

        tradeService.sellStock(WALLET_ID, STOCK_NAME);

        verify(walletService).decreaseStock(WALLET_ID, STOCK_NAME, 1);
        verify(bankService).increaseStock(STOCK_NAME, 1);
        verify(eventPublisher).publishEvent(any(TradeCompletedEvent.class));
        verify(tradeMetrics).recordSuccessfulSell();
    }

    @Test
    void sellStock_ShouldThrowException_WhenWalletHasNoStock() throws Exception {
        when(bankService.stockExists(STOCK_NAME)).thenReturn(true);
        when(walletService.getStockQuantity(WALLET_ID, STOCK_NAME)).thenReturn(0);
        when(tradeMetrics.recordTradeDuration(any())).thenAnswer(invocation -> {
            java.util.concurrent.Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });

        assertThatThrownBy(() -> tradeService.sellStock(WALLET_ID, STOCK_NAME))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(InsufficientStockInWalletException.class);

        verify(tradeMetrics).recordFailedSell();
    }
}