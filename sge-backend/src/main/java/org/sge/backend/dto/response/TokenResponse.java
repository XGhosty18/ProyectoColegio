package org.sge.backend.dto.response;

import java.util.List;

public record TokenResponse(String token, String refreshToken, String username, String email, List<String> roles) {}
