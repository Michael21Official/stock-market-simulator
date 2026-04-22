package com.remitly.stockmarket.repository;

import com.remitly.stockmarket.entity.WalletStockEntity;
import com.remitly.stockmarket.entity.WalletStockEntity.WalletStockId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletStockRepository extends JpaRepository<WalletStockEntity, WalletStockId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletStockEntity w WHERE w.id.walletId = :walletId AND w.id.stockName = :stockName")
    Optional<WalletStockEntity> findForUpdate(@Param("walletId") String walletId, @Param("stockName") String stockName);

    List<WalletStockEntity> findByIdWalletId(String walletId);

    @Modifying
    @Transactional
    @Query("UPDATE WalletStockEntity w SET w.quantity = w.quantity + :delta WHERE w.id.walletId = :walletId AND w.id.stockName = :stockName")
    int updateQuantity(@Param("walletId") String walletId, @Param("stockName") String stockName,
            @Param("delta") Integer delta);

    Optional<WalletStockEntity> findByIdWalletIdAndIdStockName(String walletId, String stockName);

    boolean existsByIdWalletIdAndIdStockName(String walletId, String stockName);
}