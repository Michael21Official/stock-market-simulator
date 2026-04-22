package com.remitly.stockmarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "wallet_stocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletStockEntity {

    @EmbeddedId
    private WalletStockId id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletStockId implements Serializable {
        @Column(name = "wallet_id")
        private String walletId;

        @Column(name = "stock_name")
        private String stockName;
    }
}