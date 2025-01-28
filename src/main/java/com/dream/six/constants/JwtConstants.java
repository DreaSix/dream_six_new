package com.dream.six.constants;

public final class JwtConstants {

    private JwtConstants() {
        throw new AssertionError("Utility class JwtConstants cannot be instantiated");
    }
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
