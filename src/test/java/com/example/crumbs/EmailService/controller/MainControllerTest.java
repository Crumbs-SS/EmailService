package com.example.crumbs.EmailService.controller;

import com.example.crumbs.EmailService.Controller.MainController;
import com.example.crumbs.EmailService.MockUtil;
import com.example.crumbs.EmailService.Service.EmailService;
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

    @Test
    public void confirmToken() throws Exception{

        mockMvc.perform(get("/email/token/{token}", MockUtil.getToken())
                .contentType("application/json"))
                .andExpect(status().isOk());

    }
    @Test
    public void sendConfirmationEmail() throws Exception{

        mockMvc.perform(get("/email/{email}/name/{name}/token/{token}", MockUtil.getEmail(), MockUtil.getName(), MockUtil.getToken())
        .contentType("application/json"))
                .andExpect(status().isOk());

    }


    @Test
    public void sendOrderDetails() throws Exception {
        mockMvc.perform(post("/email/orders/{id}/details", -1)
        .contentType("application/json"))
                .andExpect(status().isNoContent());
    }
}
