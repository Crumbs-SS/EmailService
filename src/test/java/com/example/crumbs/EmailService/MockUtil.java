package com.example.crumbs.EmailService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.crumbs.EmailService.dto.EmailDTO;

import javax.validation.constraints.Email;
import java.util.Date;

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

    public  static String createMockJWT(String role){
        final long EXPIRATION_TIME = 900_000;
        String token;
        Algorithm algorithm = Algorithm.HMAC256("MfiVzoZ/aO8N4sdd32WKC8qdIag1diSNfiZ4mtKQ8J1oaBxoCsgcXzjeH43rIwjSuKVC9BpeqEV/iUGczehBjyHH2j3ofifbQW9MquNd8mROjloyzzTGdD1iw4d5uxFV88GJcjPRo1BUvhVRbtIvKYjmeSyxA3cvpjPUinp6HMIoh0uHChrM8kUfql1WpmmSM+NyRMlMY7WGbiZ/GRCCdB8s4hzxy9baLp0ENQ==");
        token = JWT.create()
                .withClaim("role", role)
                .withSubject("correctUsername")
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);

        return token;
    }
}
