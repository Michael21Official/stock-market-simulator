package com.remitly.stockmarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_stocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankStockEntity {

    @Id
    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}