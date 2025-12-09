package com.msa.auth.application.port.out;

public interface TokenProviderPort {

    String createToken(String userId, String role);
}
