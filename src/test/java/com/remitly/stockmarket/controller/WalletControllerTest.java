package com.remitly.stockmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remitly.stockmarket.dto.TradeRequest;
import com.remitly.stockmarket.service.TradeService;
import com.remitly.stockmarket.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TradeService tradeService;

    @MockitoBean
    private WalletService walletService;

    @Test
    void trade_buy_shouldReturn200() throws Exception {
        doNothing().when(tradeService).buyStock(anyString(), anyString());

        TradeRequest request = new TradeRequest("buy");

        mockMvc.perform(post("/wallets/wallet-123/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void trade_sell_shouldReturn200() throws Exception {
        doNothing().when(tradeService).sellStock(anyString(), anyString());

        TradeRequest request = new TradeRequest("sell");

        mockMvc.perform(post("/wallets/wallet-123/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void trade_invalidType_shouldReturn400() throws Exception {
        mockMvc.perform(post("/wallets/wallet-123/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWallet_shouldReturnWallet() throws Exception {
        mockMvc.perform(get("/wallets/wallet-123"))
                .andExpect(status().isOk());
    }

    @Test
    void getStockQuantity_shouldReturnQuantity() throws Exception {
        mockMvc.perform(get("/wallets/wallet-123/stocks/AAPL"))
                .andExpect(status().isOk());
    }
}