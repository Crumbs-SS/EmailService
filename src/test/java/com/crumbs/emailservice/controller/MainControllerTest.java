package com.crumbs.emailservice.controller;

import com.crumbs.emailservice.MockUtil;
import com.crumbs.emailservice.service.EmailService;
import com.crumbs.emailservice.service.SNSService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmailService emailService;
    @MockBean
    private SNSService snsService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void confirmToken() throws Exception{
        mockMvc.perform(put("/email-service/confirmation/token/{token}", MockUtil.getToken())
                .contentType("application/json"))
                .andExpect(status().isOk());
    }
    @Test
    public void sendConfirmationEmail() throws Exception{
        mockMvc.perform(post("/email-service/confirmation/{username}", "mockUsername")
                .content(objectMapper.writeValueAsString(MockUtil.getEmailDTO()))
                .contentType("application/json"))
                .andExpect(status().isOk());
    }
    @Test
    public void sendPasswordRecoveryEmail() throws Exception {
        mockMvc.perform(post("/email-service/password/{username}", "correctUsername")
                .header("Authorization", ("Bearer " + MockUtil.createMockJWT("CUSTOMER")))
                .content(objectMapper.writeValueAsString(MockUtil.getEmailDTO()))
                .contentType("application/json"))
                .andExpect(status().isOk());
    }


    @Test
    public void sendOrderDetails() throws Exception {
        mockMvc.perform(post("/email-service/orders/{id}/details", -1)
                .header("Authorization", ("Bearer " + MockUtil.createMockJWT("CUSTOMER")))
                .header("Username" , "correctUsername")
                .contentType("application/json"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void sendOrderRequestToDriver() throws Exception {
        mockMvc.perform(post("/email-service/orders/{orderId}/drivers/{driverId}", -1, 1l)
                .header("Authorization", ("Bearer " + MockUtil.createMockJWT("ADMIN")))
                .contentType("application/json"))
                .andExpect(status().isNoContent());
    }
}
