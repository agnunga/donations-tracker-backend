package io.omosh.dts.dtos;

public record LoginRequest(
        String username,
        String password) {
}