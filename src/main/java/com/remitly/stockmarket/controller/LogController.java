package com.remitly.stockmarket.controller;

import com.remitly.stockmarket.dto.AuditLogResponse;
import com.remitly.stockmarket.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "Get audit log", description = "Returns entire audit log of successful wallet operations in order of occurrence")
    @GetMapping
    public ResponseEntity<AuditLogResponse> getAuditLog() {
        return ResponseEntity.ok(auditLogService.getAuditLog());
    }
}