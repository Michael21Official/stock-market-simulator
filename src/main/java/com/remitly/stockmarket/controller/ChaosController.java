package com.remitly.stockmarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chaos")
@RequiredArgsConstructor
public class ChaosController {

    private final ApplicationContext applicationContext;

    @Value("${server.port:8080}")
    private int port;

    @Operation(summary = "Kill this instance", description = "Terminates the application instance that receives this request")
    @PostMapping
    public ResponseEntity<Map<String, String>> chaos() {
        String instanceId = "instance-on-port-" + port;
        log.warn("☠️ Chaos endpoint called on {}. Shutting down gracefully...", instanceId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Killing " + instanceId + " gracefully...");
        response.put("status", "goodbye");

        new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("👋 Goodbye from {}!", instanceId);
            SpringApplication.exit(applicationContext, () -> 0);
        }).start();

        return ResponseEntity.ok(response);
    }
}