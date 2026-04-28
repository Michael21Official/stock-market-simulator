package com.remitly.stockmarket.repository;

import com.remitly.stockmarket.entity.BankStockEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class BankStockRepositoryTest {

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
    private BankStockRepository bankStockRepository;

    @Test
    void shouldSaveAndFindStock() {
        BankStockEntity stock = BankStockEntity.builder()
                .stockName("AAPL")
                .quantity(100)
                .build();

        bankStockRepository.save(stock);

        assertThat(bankStockRepository.findById("AAPL")).isPresent();
        assertThat(bankStockRepository.findById("AAPL").get().getQuantity()).isEqualTo(100);
    }

    @Test
    void shouldCheckIfStockExists() {
        BankStockEntity stock = BankStockEntity.builder()
                .stockName("GOOG")
                .quantity(50)
                .build();

        bankStockRepository.save(stock);

        assertThat(bankStockRepository.existsByStockName("GOOG")).isTrue();
        assertThat(bankStockRepository.existsByStockName("AAPL")).isFalse();
    }

    @Test
    void shouldUpdateStockQuantity() {
        BankStockEntity stock = BankStockEntity.builder()
                .stockName("MSFT")
                .quantity(75)
                .build();

        bankStockRepository.save(stock);

        stock.setQuantity(80);
        bankStockRepository.save(stock);

        assertThat(bankStockRepository.findById("MSFT").get().getQuantity()).isEqualTo(80);
    }

    @Test
    void shouldDeleteStock() {
        BankStockEntity stock = BankStockEntity.builder()
                .stockName("TSLA")
                .quantity(30)
                .build();

        bankStockRepository.save(stock);
        assertThat(bankStockRepository.existsByStockName("TSLA")).isTrue();

        bankStockRepository.deleteById("TSLA");
        assertThat(bankStockRepository.existsByStockName("TSLA")).isFalse();
    }
}