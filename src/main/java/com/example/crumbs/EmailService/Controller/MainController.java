package com.example.crumbs.EmailService.Controller;

import com.example.crumbs.EmailService.Service.EmailService;
import com.example.crumbs.EmailService.Service.SNSService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MainController {

    private final EmailService emailService;
    private final SNSService snsService;

    MainController(EmailService emailService, SNSService snsService){
        this.emailService = emailService;
        this.snsService = snsService;
    }

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
        snsService.sendOrderDetailsToPhoneNumber(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/orders/{orderId}/drivers/{driverId}")
    public ResponseEntity<Object> sendOrderRequestToDriver(@PathVariable Long orderId, @PathVariable Long driverId){
        snsService.sendOrderRequestToDriverPhone(driverId, orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
