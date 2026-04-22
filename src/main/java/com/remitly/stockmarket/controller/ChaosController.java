package com.remitly.stockmarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;

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
    public ResponseEntity<String> chaos() {
        log.warn("Chaos endpoint called on instance with port {}. Shutting down...", port);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log.info("Goodbye from instance on port {}!", port);
                System.exit(0);
            });
        }

        return ResponseEntity.ok("Killing instance on port " + port + " gracefully...");
    }
}