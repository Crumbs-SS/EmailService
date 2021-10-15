package com.crumbs.emailservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceTest {
    @Autowired EmailService emailService;

    @Test
    void sendOrderDetails() {
        assertThrows(NoSuchElementException.class, () -> emailService.sendOrderDetails(-1L));
    }

    @Test
    void confirmToken() {

    }
}