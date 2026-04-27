package com.remitly.stockmarket.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class TradeMetrics {

    private final Counter successfulBuys;
    private final Counter successfulSells;
    private final Counter failedBuys;
    private final Counter failedSells;
    private final Timer tradeDuration;

    public TradeMetrics(MeterRegistry registry) {
        this.successfulBuys = Counter.builder("trades.buy.success")
                .description("Number of successful buy operations")
                .register(registry);

        this.successfulSells = Counter.builder("trades.sell.success")
                .description("Number of successful sell operations")
                .register(registry);

        this.failedBuys = Counter.builder("trades.buy.failure")
                .description("Number of failed buy operations")
                .register(registry);

        this.failedSells = Counter.builder("trades.sell.failure")
                .description("Number of failed sell operations")
                .register(registry);

        this.tradeDuration = Timer.builder("trades.duration")
                .description("Time taken to process trades")
                .register(registry);
    }

    public void recordSuccessfulBuy() {
        successfulBuys.increment();
    }

    public void recordSuccessfulSell() {
        successfulSells.increment();
    }

    public void recordFailedBuy() {
        failedBuys.increment();
    }

    public void recordFailedSell() {
        failedSells.increment();
    }

    public <T> T recordTradeDuration(java.util.concurrent.Callable<T> callable) throws Exception {
        return tradeDuration.recordCallable(callable);
    }
}