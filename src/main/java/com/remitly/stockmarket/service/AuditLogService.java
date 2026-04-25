package com.remitly.stockmarket.service;

import com.remitly.stockmarket.dto.AuditLogResponse;
import com.remitly.stockmarket.entity.AuditLogEntity;
import com.remitly.stockmarket.event.TradeCompletedEvent;
import com.remitly.stockmarket.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTradeCompleted(TradeCompletedEvent event) {
        log.info("Audit log event received: {} - {} - {}", event.getOperationType(), event.getWalletId(),
                event.getStockName());

        AuditLogEntity logEntry = AuditLogEntity.builder()
                .operationType(event.getOperationType())
                .walletId(event.getWalletId())
                .stockName(event.getStockName())
                .build();

        auditLogRepository.save(logEntry);
        auditLogRepository.flush();
        log.info("Audit log saved successfully");
    }

    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLog() {
        List<AuditLogEntity> logs = auditLogRepository.findAllOrderByCreatedAtAsc();

        List<AuditLogResponse.LogEntry> logEntries = logs.stream()
                .map(log -> AuditLogResponse.LogEntry.builder()
                        .operationType(log.getOperationType())
                        .walletId(log.getWalletId())
                        .stockName(log.getStockName())
                        .build())
                .collect(Collectors.toList());

        log.info("Returning {} audit log entries", logEntries.size());
        return AuditLogResponse.builder().log(logEntries).build();
    }
}