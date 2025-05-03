package io.omosh.dts.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token_records")
public class RefreshTokenRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken; // The actual refresh token string
    private String username; // Username or user ID

    private LocalDateTime issuedAt; // Timestamp of when the token was issued

    private LocalDateTime expiration; // Expiration date of the refresh token

    private String userAgent; // Optional: The user-agent (browser/device) the token was issued for

    private String ipAddress; // Optional: The IP address the token was issued from

    private boolean revoked; // Whether the token has been revoked

    public RefreshTokenRecord(String refreshToken, String username, LocalDateTime issuedAt, LocalDateTime expiration, String userAgent, String ipAddress) {
        this.refreshToken = refreshToken;
        this.username = username;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.revoked = false; // Defaults to false, indicating it's not revoked
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiration);
    }

    public void revoke() {
        this.revoked = true;
    }
}