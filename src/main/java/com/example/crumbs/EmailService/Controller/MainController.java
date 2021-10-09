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
@RequestMapping("/email-service")
@PreAuthorize("isAuthenticated()")
public class MainController {

    private final EmailService emailService;
    private final SNSService snsService;

    MainController(EmailService emailService, SNSService snsService){
        this.emailService = emailService;
        this.snsService = snsService;
    }
    @PreAuthorize("permitAll()")
    @PutMapping("/confirmation/token/{token}")
    public String confirmToken(@PathVariable String token){
            return emailService.confirmToken(token);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/confirmation/{username}")
    public void sendConfirmationEmail(@PathVariable String username, @Validated @RequestBody EmailDTO emailDTO) {
        emailService.sendConfirmationEmail(emailDTO);
    }

    @PreAuthorize("hasAuthority('CUSTOMER') and #username == authentication.principal")
    @PostMapping("/password/{username}")
    public void sendPasswordRecoveryEmail(@PathVariable String username, @Validated @RequestBody EmailDTO emailDTO) {
        emailService.sendPasswordRecoveryEmail(emailDTO);
    }

    @PreAuthorize("hasAuthority('CUSTOMER') and #username == authentication.principal")
    @PostMapping("/orders/{id}/details")
    public ResponseEntity<Object> sendOrderDetails(
            @RequestHeader("Username") String username,
            @PathVariable Long id
    ){
        System.out.println("Hello World");
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
