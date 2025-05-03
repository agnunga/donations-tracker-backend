package io.omosh.dts;

import io.r2dbc.spi.*;
import reactor.core.publisher.Mono;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class DriverTest {

    public static void main(String[] args) {
        // Define connection options
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mariadb")
                .option(HOST, "localhost")
                .option(PORT, 3306)
                .option(USER, "root")
                .option(PASSWORD, "")
                .option(DATABASE, "donations2")
                .build();

        // Get the connection factory
        ConnectionFactory connectionFactory = ConnectionFactories.get(options);

        // Try to connect, then close, then print results — all reactively
        Mono<Void> testConnection = Mono.from(connectionFactory.create())
                .doOnNext(conn -> System.out.println("Successfully connected to MariaDB via R2DBC!"))
                .flatMap(conn -> Mono.from(conn.close()));

        // Block only once, outside the reactive pipeline
        testConnection
                .doOnTerminate(() -> System.out.println("🔚 Connection attempt completed"))
                .doOnError(e -> {
                    System.out.println(" Failed to connect or close connection");
                    e.printStackTrace();
                })
                .block(); // Safe because it's top-level, not inside reactive thread
    }
}
