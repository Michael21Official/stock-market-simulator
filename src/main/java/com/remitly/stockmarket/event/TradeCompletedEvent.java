package com.remitly.stockmarket.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TradeCompletedEvent {
    private final String operationType;
    private final String walletId;
    private final String stockName;
}