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
public class AuditLogResponse {

    private List<LogEntry> log;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogEntry {
        @JsonProperty("type")
        private String operationType;

        @JsonProperty("wallet_id")
        private String walletId;

        @JsonProperty("stock_name")
        private String stockName;
    }
}