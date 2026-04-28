package com.remitly.stockmarket.service;

import com.remitly.stockmarket.dto.BankStocksRequest;
import com.remitly.stockmarket.dto.BankStocksResponse;
import com.remitly.stockmarket.entity.BankStockEntity;
import com.remitly.stockmarket.exception.InsufficientStockInBankException;
import com.remitly.stockmarket.exception.StockNotFoundException;
import com.remitly.stockmarket.repository.BankStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private BankStockRepository bankStockRepository;

    @InjectMocks
    private BankService bankService;

    private static final String STOCK_NAME = "AAPL";
    private static final int INITIAL_QUANTITY = 100;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getBankStocks_ShouldReturnAllStocks() {
        List<BankStockEntity> stocks = List.of(
                BankStockEntity.builder().stockName("AAPL").quantity(100).build(),
                BankStockEntity.builder().stockName("GOOG").quantity(50).build());
        when(bankStockRepository.findAll()).thenReturn(stocks);

        BankStocksResponse response = bankService.getBankStocks();

        assertThat(response.getBankStocks()).hasSize(2);
        assertThat(response.getBankStocks().get(0).getStockName()).isEqualTo("AAPL");
        assertThat(response.getBankStocks().get(0).getQuantity()).isEqualTo(100);
    }

    @Test
    void setBankStocks_ShouldDeleteOldAndSaveNew() {
        BankStocksRequest request = BankStocksRequest.builder()
                .stocks(List.of(
                        BankStocksRequest.StockItem.builder().name("AAPL").quantity(100).build()))
                .build();

        bankService.setBankStocks(request);

        verify(bankStockRepository).deleteAll();
        verify(bankStockRepository).saveAll(any());
    }

    @Test
    void decreaseStock_ShouldDecreaseQuantity_WhenStockExists() {
        BankStockEntity stock = BankStockEntity.builder().stockName(STOCK_NAME).quantity(INITIAL_QUANTITY).build();
        when(bankStockRepository.findForUpdate(STOCK_NAME)).thenReturn(Optional.of(stock));

        bankService.decreaseStock(STOCK_NAME, 1);

        assertThat(stock.getQuantity()).isEqualTo(99);
        verify(bankStockRepository).save(stock);
    }

    @Test
    void decreaseStock_ShouldThrowException_WhenStockNotFound() {
        when(bankStockRepository.findForUpdate("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.decreaseStock("UNKNOWN", 1))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    void decreaseStock_ShouldThrowException_WhenInsufficientQuantity() {
        BankStockEntity stock = BankStockEntity.builder().stockName(STOCK_NAME).quantity(0).build();
        when(bankStockRepository.findForUpdate(STOCK_NAME)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> bankService.decreaseStock(STOCK_NAME, 1))
                .isInstanceOf(InsufficientStockInBankException.class);
    }

    @Test
    void increaseStock_ShouldIncreaseQuantity_WhenStockExists() {
        BankStockEntity stock = BankStockEntity.builder().stockName(STOCK_NAME).quantity(INITIAL_QUANTITY).build();
        when(bankStockRepository.findForUpdate(STOCK_NAME)).thenReturn(Optional.of(stock));

        bankService.increaseStock(STOCK_NAME, 1);

        assertThat(stock.getQuantity()).isEqualTo(101);
        verify(bankStockRepository).save(stock);
    }

    @Test
    void increaseStock_ShouldCreateStock_WhenNotExists() {
        BankStockEntity newStock = BankStockEntity.builder().stockName(STOCK_NAME).quantity(0).build();
        when(bankStockRepository.findForUpdate(STOCK_NAME)).thenReturn(Optional.empty());
        when(bankStockRepository.save(any(BankStockEntity.class))).thenReturn(newStock);

        bankService.increaseStock(STOCK_NAME, 1);

        verify(bankStockRepository).save(any(BankStockEntity.class));
    }

    @Test
    void getStockQuantity_ShouldReturnZero_WhenStockNotFound() {
        when(bankStockRepository.findById(STOCK_NAME)).thenReturn(Optional.empty());

        int quantity = bankService.getStockQuantity(STOCK_NAME);

        assertThat(quantity).isZero();
    }

    @Test
    void stockExists_ShouldReturnTrue_WhenStockExists() {
        when(bankStockRepository.existsByStockName(STOCK_NAME)).thenReturn(true);

        boolean exists = bankService.stockExists(STOCK_NAME);

        assertThat(exists).isTrue();
    }
}