package com.remitly.stockmarket.service;

import com.remitly.stockmarket.dto.BankStocksRequest;
import com.remitly.stockmarket.dto.BankStocksResponse;
import com.remitly.stockmarket.entity.BankStockEntity;
import com.remitly.stockmarket.exception.InsufficientStockInBankException;
import com.remitly.stockmarket.exception.StockNotFoundException;
import com.remitly.stockmarket.repository.BankStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

    private final BankStockRepository bankStockRepository;

    @Cacheable(value = "bankStocks")
    @Transactional(readOnly = true)
    public BankStocksResponse getBankStocks() {
        List<BankStockEntity> stocks = bankStockRepository.findAll();

        List<BankStocksResponse.BankStockItem> stockItems = stocks.stream()
                .map(stock -> BankStocksResponse.BankStockItem.builder()
                        .stockName(stock.getStockName())
                        .quantity(stock.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return BankStocksResponse.builder().bankStocks(stockItems).build();
    }

    @CacheEvict(value = "bankStocks", allEntries = true)
    @Transactional
    public void setBankStocks(BankStocksRequest request) {
        bankStockRepository.deleteAll();

        List<BankStockEntity> entities = request.getStocks().stream()
                .map(item -> BankStockEntity.builder()
                        .stockName(item.getName())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        bankStockRepository.saveAll(entities);
        log.info("Bank state updated with {} stocks", entities.size());
    }

    @Transactional
    public void decreaseStock(String stockName, int quantity) {
        BankStockEntity stock = bankStockRepository.findForUpdate(stockName)
                .orElseThrow(() -> new StockNotFoundException(stockName));

        if (stock.getQuantity() < quantity) {
            throw new InsufficientStockInBankException(stockName, quantity, stock.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        bankStockRepository.save(stock);
    }

    @Transactional
    public void increaseStock(String stockName, int quantity) {
        BankStockEntity stock = bankStockRepository.findForUpdate(stockName)
                .orElseGet(() -> BankStockEntity.builder()
                        .stockName(stockName)
                        .quantity(0)
                        .build());

        stock.setQuantity(stock.getQuantity() + quantity);
        bankStockRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public int getStockQuantity(String stockName) {
        return bankStockRepository.findById(stockName)
                .map(BankStockEntity::getQuantity)
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public boolean stockExists(String stockName) {
        return bankStockRepository.existsByStockName(stockName);
    }
}