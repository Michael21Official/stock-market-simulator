package com.remitly.stockmarket.controller;

import com.remitly.stockmarket.dto.TradeRequest;
import com.remitly.stockmarket.dto.WalletResponse;
import com.remitly.stockmarket.service.TradeService;
import com.remitly.stockmarket.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final TradeService tradeService;
    private final WalletService walletService;

    @Operation(summary = "Buy or sell a stock", description = "If wallet doesn't exist, it will be created. " +
            "Returns 400 if insufficient stock in bank (buy) or wallet (sell).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Stock does not exist")
    })
    @PostMapping("/{walletId}/stocks/{stockName}")
    public ResponseEntity<Void> trade(
            @Parameter(description = "Wallet ID", example = "wallet-123") @PathVariable String walletId,
            @Parameter(description = "Stock name", example = "AAPL") @PathVariable String stockName,
            @Valid @RequestBody TradeRequest request) {

        if ("buy".equalsIgnoreCase(request.type())) {
            tradeService.buyStock(walletId, stockName);
        } else {
            tradeService.sellStock(walletId, stockName);
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get wallet state", description = "Returns all stocks and quantities in the specified wallet")
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(
            @Parameter(description = "Wallet ID", example = "wallet-123") @PathVariable String walletId) {

        return ResponseEntity.ok(walletService.getWallet(walletId));
    }

    @Operation(summary = "Get stock quantity in wallet", description = "Returns quantity of specific stock in the specified wallet")
    @GetMapping("/{walletId}/stocks/{stockName}")
    public ResponseEntity<Integer> getStockQuantity(
            @Parameter(description = "Wallet ID", example = "wallet-123") @PathVariable String walletId,
            @Parameter(description = "Stock name", example = "AAPL") @PathVariable String stockName) {

        return ResponseEntity.ok(walletService.getStockQuantity(walletId, stockName));
    }
}