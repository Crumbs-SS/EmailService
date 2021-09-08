package com.example.crumbs.EmailService.Controller;

import com.example.crumbs.EmailService.Service.EmailService;
import com.example.crumbs.EmailService.Service.SNSService;
import com.example.crumbs.EmailService.dto.EmailDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PutMapping("/email/token/{token}")
    public String confirmToken(@PathVariable String token){
            return emailService.confirmToken(token);
    }

    @PreAuthorize("hasAuthority('CUSTOMER') and #username == authentication.principal")
    @PostMapping("/email/confirmation/{username}")
    public void sendConfirmationEmail(@PathVariable String username, @Validated @RequestBody EmailDTO emailDTO) {
        emailService.sendConfirmationEmail(emailDTO);
    }

    @PreAuthorize("hasAuthority('CUSTOMER') and #username == authentication.principal")
    @PostMapping("/email/password/{username}")
    public void sendPasswordRecoveryEmail(@PathVariable String username, @Validated @RequestBody EmailDTO emailDTO) {
        emailService.sendPasswordRecoveryEmail(emailDTO);
    }

    @PreAuthorize("hasAuthority('CUSTOMER') and #username == authentication.principal")
    @PostMapping("/email/{username}/orders/{id}/details")
    public ResponseEntity<Object> sendOrderDetails(@PathVariable String username, @PathVariable Long id){
        emailService.sendOrderDetails(id);
        snsService.sendOrderDetailsToPhoneNumber(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/orders/{orderId}/drivers/{driverId}")
    public ResponseEntity<Object> sendOrderRequestToDriver(@PathVariable Long orderId, @PathVariable Long driverId){
        snsService.sendOrderRequestToDriverPhone(driverId, orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
