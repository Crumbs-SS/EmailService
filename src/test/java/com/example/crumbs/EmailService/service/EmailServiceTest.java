package com.example.crumbs.EmailService.service;

import com.crumbs.lib.entity.UserStatus;
import com.crumbs.lib.repository.ConfirmationTokenRepository;
import com.crumbs.lib.repository.UserStatusRepository;
import com.example.crumbs.EmailService.Service.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    UserStatusRepository userStatusRepository;

    @Test
    void confirmToken() throws Exception{


    }
    @Test
    void sendConfirmationEmail() throws Exception{

    }
}
