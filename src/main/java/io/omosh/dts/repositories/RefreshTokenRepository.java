package io.omosh.dts.repositories;

import io.omosh.dts.models.RefreshTokenRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshTokenRecord, Long> {

    Mono<RefreshTokenRecord> findByRefreshToken(String refreshToken);

    Mono<RefreshTokenRecord> findByUsernameAndRevokedFalse(String username);

    List<RefreshTokenRecord> findByRevokedFalseAndExpirationBefore(LocalDateTime dateTime);

}
