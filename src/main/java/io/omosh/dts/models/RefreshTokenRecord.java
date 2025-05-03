package io.omosh.dts.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("refresh_token_records")
public class RefreshTokenRecord {

    @Id
    private Long id;

    private String refreshToken; // The actual refresh token string
    private String username; // Username or user ID

    @Column()
    private LocalDateTime issuedAt; // Timestamp of when the token was issued

    @Column()
    private LocalDateTime expiration; // Expiration date of the refresh token

    private String userAgent; // Optional: The user-agent (browser/device) the token was issued for

    private String ipAddress; // Optional: The IP address the token was issued from

    private boolean revoked; // Whether the token has been revoked

    public RefreshTokenRecord() {
    }

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
