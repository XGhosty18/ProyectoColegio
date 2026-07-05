package org.sge.backend.dto.response;

public record TokenResponse(String token, String type, String username, String role) {}
