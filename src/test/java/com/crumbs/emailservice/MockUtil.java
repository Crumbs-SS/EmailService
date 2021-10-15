package com.crumbs.emailservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.crumbs.emailservice.dto.EmailDTO;
import com.crumbs.lib.entity.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public class MockUtil {

    public static String getToken(){
        return "mockToken";
    }
    public static String getEmail(){
        return "mockEmail";
    }
    public static String getName(){
        return "mockName";
    }

    public static EmailDTO getEmailDTO(){
        return EmailDTO.builder().email("mock@a.com").name("mockUsername").token("mockTocken").build();
    }
    public static ConfirmationToken getConfirmationToken(){
        return ConfirmationToken.builder()
                .token("token")
                .createdAt(LocalDateTime.now())
                .id(1l)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
    }

    public  static String createMockJWT(String role){
        final long EXPIRATION_TIME = 900_000;
        String token;
        Algorithm algorithm = Algorithm.HMAC256(System.getenv("JWT_SECRET"));
        token = JWT.create()
                .withAudience("crumbs")
                .withIssuer("Crumbs")
                .withClaim("role", role)
                .withSubject("correctUsername")
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);

        return token;
    }
}
