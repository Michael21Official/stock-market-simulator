package com.remitly.stockmarket.service;

import com.remitly.stockmarket.dto.WalletResponse;
import com.remitly.stockmarket.entity.WalletEntity;
import com.remitly.stockmarket.entity.WalletStockEntity;
import com.remitly.stockmarket.exception.InsufficientStockInWalletException;
import com.remitly.stockmarket.exception.WalletNotFoundException;
import com.remitly.stockmarket.repository.WalletRepository;
import com.remitly.stockmarket.repository.WalletStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletStockRepository walletStockRepository;

    @Transactional
    public void ensureWalletExists(String walletId) {
        if (!walletRepository.existsById(walletId)) {
            WalletEntity wallet = WalletEntity.builder().id(walletId).build();
            walletRepository.save(wallet);
            log.debug("Created new wallet: {}", walletId);
        }
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(String walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException(walletId);
        }

        List<WalletStockEntity> stocks = walletStockRepository.findByIdWalletId(walletId);

        List<WalletResponse.StockHolding> holdings = stocks.stream()
                .map(stock -> WalletResponse.StockHolding.builder()
                        .stockName(stock.getId().getStockName())
                        .quantity(stock.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return WalletResponse.builder()
                .id(walletId)
                .stocks(holdings)
                .build();
    }

    @Transactional(readOnly = true)
    public int getStockQuantity(String walletId, String stockName) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException(walletId);
        }

        return walletStockRepository.findByIdWalletIdAndIdStockName(walletId, stockName)
                .map(WalletStockEntity::getQuantity)
                .orElse(0);
    }

    @Transactional
    public void increaseStock(String walletId, String stockName, int quantity) {
        ensureWalletExists(walletId);

        int updated = walletStockRepository.updateQuantity(walletId, stockName, quantity);

        if (updated == 0) {
            WalletStockEntity.WalletStockId id = new WalletStockEntity.WalletStockId(walletId, stockName);
            WalletStockEntity newStock = WalletStockEntity.builder()
                    .id(id)
                    .quantity(quantity)
                    .build();
            walletStockRepository.save(newStock);
        }
    }

    @Transactional
    public void decreaseStock(String walletId, String stockName, int quantity) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException(walletId);
        }

        WalletStockEntity stock = walletStockRepository.findForUpdate(walletId, stockName)
                .orElseThrow(() -> new InsufficientStockInWalletException(walletId, stockName, quantity, 0));

        if (stock.getQuantity() < quantity) {
            throw new InsufficientStockInWalletException(walletId, stockName, quantity, stock.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        walletStockRepository.save(stock);
    }
}