package ru.jpoint.transactionslocksapp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TransactionsLocksAppApplication.class)
@ContextConfiguration(classes = AbstractIntegrationTest.TestConfig.class)
public abstract class AbstractIntegrationTest {

    @Container
    private static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:12.10")
            .withDatabaseName("database")
            .withUsername("postgres")
            .withPassword("password");

    @Container
    private static GenericContainer REDIS_CONTAINER = new GenericContainer(DockerImageName.parse("redis:7.0.0"))
            .withExposedPorts(6379);

    @Container
    private static KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.1.1"))
            .withEmbeddedZookeeper();

    @Test
    void contextLoads() {
        assertTrue(POSTGRESQL_CONTAINER.isRunning());
        assertTrue(REDIS_CONTAINER.isRunning());
        assertTrue(KAFKA_CONTAINER.isRunning());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public DataSource dataSource() {
            var hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(POSTGRESQL_CONTAINER.getJdbcUrl());
            hikariConfig.setUsername(POSTGRESQL_CONTAINER.getUsername());
            hikariConfig.setPassword(POSTGRESQL_CONTAINER.getPassword());
            hikariConfig.setTransactionIsolation("TRANSACTION_READ_UNCOMMITTED");

            return new HikariDataSource(hikariConfig);
        }

        @Bean
        @Primary
        public RedisStandaloneConfiguration redis() {
            RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
            redisConfiguration.setDatabase(0);
            redisConfiguration.setPort(REDIS_CONTAINER.getFirstMappedPort());
            redisConfiguration.setHostName(REDIS_CONTAINER.getHost());

            return redisConfiguration;
        }
    }
}
