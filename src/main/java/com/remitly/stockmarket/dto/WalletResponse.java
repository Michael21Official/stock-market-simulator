package com.remitly.stockmarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private String id;
    private List<StockHolding> stocks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockHolding {
        @JsonProperty("name")
        private String stockName;

        private Integer quantity;
    }
}