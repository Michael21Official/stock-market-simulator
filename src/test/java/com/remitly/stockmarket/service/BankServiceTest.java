package com.remitly.stockmarket.service;

import com.remitly.stockmarket.dto.BankStocksRequest;
import com.remitly.stockmarket.dto.BankStocksResponse;
import com.remitly.stockmarket.entity.BankStockEntity;
import com.remitly.stockmarket.exception.InsufficientStockInBankException;
import com.remitly.stockmarket.exception.StockNotFoundException;
import com.remitly.stockmarket.repository.BankStockRepository;
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

    @Test
    void getBankStocks_ShouldReturnAllStocks() {
        // Given
        List<BankStockEntity> stocks = List.of(
                BankStockEntity.builder().stockName("AAPL").quantity(100).build(),
                BankStockEntity.builder().stockName("GOOG").quantity(50).build());
        when(bankStockRepository.findAll()).thenReturn(stocks);

        // When
        BankStocksResponse response = bankService.getBankStocks();

        // Then
        assertThat(response.getBankStocks()).hasSize(2);
        assertThat(response.getBankStocks().get(0).getStockName()).isEqualTo("AAPL");
        assertThat(response.getBankStocks().get(0).getQuantity()).isEqualTo(100);
    }

    @Test
    void setBankStocks_ShouldDeleteOldAndSaveNew() {
        // Given
        BankStocksRequest request = BankStocksRequest.builder()
                .stocks(List.of(
                        BankStocksRequest.StockItem.builder().name("AAPL").quantity(100).build()))
                .build();

        // When
        bankService.setBankStocks(request);

        // Then
        verify(bankStockRepository).deleteAll();
        verify(bankStockRepository).saveAll(any());
    }

    @Test
    void decreaseStock_ShouldDecreaseQuantity_WhenStockExists() {
        // Given
        BankStockEntity stock = BankStockEntity.builder().stockName("AAPL").quantity(100).build();
        when(bankStockRepository.findForUpdate("AAPL")).thenReturn(Optional.of(stock));

        // When
        bankService.decreaseStock("AAPL", 1);

        // Then
        assertThat(stock.getQuantity()).isEqualTo(99);
        verify(bankStockRepository).save(stock);
    }

    @Test
    void decreaseStock_ShouldThrowException_WhenStockNotFound() {
        // Given
        when(bankStockRepository.findForUpdate("UNKNOWN")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bankService.decreaseStock("UNKNOWN", 1))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    void decreaseStock_ShouldThrowException_WhenInsufficientQuantity() {
        // Given
        BankStockEntity stock = BankStockEntity.builder().stockName("AAPL").quantity(0).build();
        when(bankStockRepository.findForUpdate("AAPL")).thenReturn(Optional.of(stock));

        // When & Then
        assertThatThrownBy(() -> bankService.decreaseStock("AAPL", 1))
                .isInstanceOf(InsufficientStockInBankException.class);
    }
}