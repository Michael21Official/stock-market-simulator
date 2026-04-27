package com.remitly.stockmarket.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class TradeIntegrationTest {

        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
                        .withDatabaseName("testdb")
                        .withUsername("test")
                        .withPassword("test");

        @DynamicPropertySource
        static void properties(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
        }

        @Autowired
        private TestRestTemplate restTemplate;

        private HttpHeaders headers;

        @BeforeEach
        void setUp() {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
        }

        private void waitForBankStock(String expectedStockName, int expectedQuantity, int maxRetries, long retryDelayMs)
                        throws InterruptedException {
                for (int i = 0; i < maxRetries; i++) {
                        ResponseEntity<String> response = restTemplate.getForEntity("/stocks", String.class);
                        String body = response.getBody();
                        if (body.contains("\"name\":\"" + expectedStockName + "\",\"quantity\":" + expectedQuantity) ||
                                        body.contains("\"quantity\":" + expectedQuantity + ",\"name\":\""
                                                        + expectedStockName + "\"")) {
                                return;
                        }
                        Thread.sleep(retryDelayMs);
                }
                ResponseEntity<String> response = restTemplate.getForEntity("/stocks", String.class);
                assertThat(response.getBody())
                                .contains("\"name\":\"" + expectedStockName + "\"")
                                .contains("\"quantity\":" + expectedQuantity);
        }

        @Test
        void shouldBuyStock() throws InterruptedException {
                String walletId = "wallet-buy-" + UUID.randomUUID().toString().substring(0, 8);

                Map<String, Object> bankRequest = Map.of("stocks",
                                List.of(Map.of("name", "AAPL", "quantity", 100)));
                restTemplate.exchange("/stocks", HttpMethod.POST, new HttpEntity<>(bankRequest, headers), Void.class);

                restTemplate.exchange("/wallets/" + walletId + "/stocks/AAPL", HttpMethod.POST,
                                new HttpEntity<>(Map.of("type", "buy"), headers), Void.class);

                waitForBankStock("AAPL", 99, 20, 500);

                ResponseEntity<String> wallet = restTemplate.getForEntity("/wallets/" + walletId, String.class);
                assertThat(wallet.getBody()).contains("AAPL").contains("1");

                ResponseEntity<String> log = restTemplate.getForEntity("/log", String.class);
                assertThat(log.getBody()).contains("buy").contains(walletId);
        }

        @Test
        void shouldSellStock() throws InterruptedException {
                String walletId = "wallet-sell-" + UUID.randomUUID().toString().substring(0, 8);

                Map<String, Object> bankRequest = Map.of("stocks",
                                List.of(Map.of("name", "AAPL", "quantity", 100)));
                restTemplate.exchange("/stocks", HttpMethod.POST, new HttpEntity<>(bankRequest, headers), Void.class);

                restTemplate.exchange("/wallets/" + walletId + "/stocks/AAPL", HttpMethod.POST,
                                new HttpEntity<>(Map.of("type", "buy"), headers), Void.class);

                Thread.sleep(500);
                waitForBankStock("AAPL", 99, 20, 500);

                ResponseEntity<Void> sellResp = restTemplate.exchange("/wallets/" + walletId + "/stocks/AAPL",
                                HttpMethod.POST, new HttpEntity<>(Map.of("type", "sell"), headers), Void.class);
                assertThat(sellResp.getStatusCode()).isEqualTo(HttpStatus.OK);

                Thread.sleep(500);
                waitForBankStock("AAPL", 100, 20, 500);

                ResponseEntity<String> walletAfter = restTemplate.getForEntity("/wallets/" + walletId, String.class);
                assertThat(walletAfter.getBody()).doesNotContain("\"quantity\":1");

                ResponseEntity<String> log = restTemplate.getForEntity("/log", String.class);
                assertThat(log.getBody()).contains("sell").contains(walletId);
        }

        @Test
        void shouldReturn500WhenStockDoesNotExist() {
                ResponseEntity<String> response = restTemplate.exchange(
                                "/wallets/wallet-404/stocks/UNKNOWN", HttpMethod.POST,
                                new HttpEntity<>(Map.of("type", "buy"), headers),
                                String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Test
        void shouldReturn500WhenBuyingStockNotInBank() {
                restTemplate.exchange("/stocks", HttpMethod.POST,
                                new HttpEntity<>(Map.of("stocks", List.of()), headers), Void.class);

                ResponseEntity<String> response = restTemplate.exchange(
                                "/wallets/wallet-400/stocks/AAPL", HttpMethod.POST,
                                new HttpEntity<>(Map.of("type", "buy"), headers),
                                String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
}