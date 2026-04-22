package com.remitly.stockmarket.controller;

import com.remitly.stockmarket.dto.BankStocksRequest;
import com.remitly.stockmarket.dto.BankStocksResponse;
import com.remitly.stockmarket.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final BankService bankService;

    @Operation(summary = "Get bank state", description = "Returns all stocks and quantities available in the bank")
    @GetMapping
    public ResponseEntity<BankStocksResponse> getBankStocks() {
        return ResponseEntity.ok(bankService.getBankStocks());
    }

    @Operation(summary = "Set bank state", description = "Overwrites the entire bank state with the provided stocks")
    @ApiResponse(responseCode = "200", description = "Bank state updated successfully")
    @PostMapping
    public ResponseEntity<Void> setBankStocks(@Valid @RequestBody BankStocksRequest request) {
        bankService.setBankStocks(request);
        return ResponseEntity.ok().build();
    }
}