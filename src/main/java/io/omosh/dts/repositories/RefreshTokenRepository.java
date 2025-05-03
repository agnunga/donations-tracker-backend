package io.omosh.dts.repositories;

import io.omosh.dts.models.RefreshTokenRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenRecord, Long> {

    Optional<RefreshTokenRecord> findByRefreshToken(String refreshToken);

    Optional<RefreshTokenRecord> findByUsernameAndRevokedFalse(String username);

    List<RefreshTokenRecord> findByRevokedFalseAndExpirationBefore(LocalDateTime dateTime);

}