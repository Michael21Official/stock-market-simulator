package com.remitly.stockmarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("walletId")
    @JoinColumn(name = "wallet_id")
    private WalletEntity wallet;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletStockId implements java.io.Serializable {
        @Column(name = "wallet_id")
        private String walletId;

        @Column(name = "stock_name")
        private String stockName;
    }
}