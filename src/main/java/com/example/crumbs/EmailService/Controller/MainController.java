package com.example.crumbs.EmailService.Controller;

import com.example.crumbs.EmailService.Service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MainController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/email/token/{token}")
    public String confirmToken(@PathVariable String token){
            return emailService.confirmToken(token);
    }

    @GetMapping("/email/{email}/name/{name}/token/{token}")
    public void sendConfirmationEmail(@PathVariable String email, @PathVariable String name, @PathVariable String token) {
        emailService.sendConfirmationEmail(email, name, token);
    }

    @PostMapping("/email/orders/{id}/details")
    public ResponseEntity<Object> sendOrderDetails(@PathVariable Long id){
        emailService.sendOrderDetails(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
