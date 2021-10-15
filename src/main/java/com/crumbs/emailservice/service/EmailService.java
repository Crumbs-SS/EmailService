package com.crumbs.emailservice.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.crumbs.emailservice.mapper.TemplateData;
import com.crumbs.emailservice.util.ApiUtil;
import com.crumbs.lib.entity.ConfirmationToken;
import com.crumbs.lib.entity.Order;
import com.crumbs.lib.entity.UserDetails;
import com.crumbs.lib.entity.UserStatus;
import com.crumbs.lib.repository.ConfirmationTokenRepository;
import com.crumbs.lib.repository.OrderRepository;
import com.crumbs.lib.repository.UserStatusRepository;
import com.crumbs.emailservice.dto.EmailDTO;
import com.crumbs.emailservice.mapper.TemplateDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(rollbackFor = { Exception.class })
@Slf4j
public class EmailService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserStatusRepository userStatusRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AWSCredentials credentials = new BasicAWSCredentials(ApiUtil.getAWS_ACCESS_KEY_ID(), ApiUtil.getAWS_SECRET_ACCESS_KEY());
    private final AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion("us-east-1").build();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public EmailService(
            ConfirmationTokenRepository confirmationTokenRepository,
            UserStatusRepository userStatusRepository,
            OrderRepository orderRepository
            ){
        this.confirmationTokenRepository =  confirmationTokenRepository;
        this.userStatusRepository = userStatusRepository;
        this.orderRepository = orderRepository;
    }

    public void sendConfirmationEmail(EmailDTO emailDTO) {

        Destination destination = new Destination();
        List<String> toAddresses = new ArrayList<>();
        toAddresses.add(emailDTO.getEmail());

        destination.setToAddresses(toAddresses);
        SendTemplatedEmailRequest templatedEmailRequest = new SendTemplatedEmailRequest();
        templatedEmailRequest.withDestination(destination);
        String emailConfirmationTemplate = "EmailConfirmationTemplate";
        templatedEmailRequest.withTemplate(emailConfirmationTemplate);

        String link = ApiUtil.getCLIENT_URL() + "/email/verification/" + emailDTO.getToken();

        String templateData = "{ \"name\":\"" + emailDTO.getName() + "\", \"link\": \""+ link + "\"}";

        templatedEmailRequest.withTemplateData(templateData);
        templatedEmailRequest.withSource(ApiUtil.getFROM());
        client.sendTemplatedEmail(templatedEmailRequest);
    }

    public void sendPasswordRecoveryEmail(EmailDTO emailDTO) {
        Destination destination = new Destination();
        List<String> toAddresses = new ArrayList<>();
        toAddresses.add(emailDTO.getEmail());

        destination.setToAddresses(toAddresses);
        SendTemplatedEmailRequest templatedEmailRequest = new SendTemplatedEmailRequest();
        templatedEmailRequest.withDestination(destination);
        String passwordRecoveryTemplate = "passwordRecoveryTemplate";
        templatedEmailRequest.withTemplate(passwordRecoveryTemplate);

        String link = ApiUtil.getCLIENT_URL() + "/passwordRecovery/" + emailDTO.getToken();

        String templateData = "{\"link\": \""+ link + "\"}";

        templatedEmailRequest.withTemplateData(templateData);
        templatedEmailRequest.withSource(ApiUtil.getFROM());
        client.sendTemplatedEmail(templatedEmailRequest);
    }

    public void sendOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        UserDetails customer = order.getCustomer().getUserDetails();
        String email = customer.getEmail();
        TemplateData templateData = TemplateDataMapper.orderToTemplateData(order);

        Destination destination = new Destination(List.of(email));
        SendTemplatedEmailRequest templatedEmailRequest = new SendTemplatedEmailRequest();
        templatedEmailRequest.withDestination(destination);
        templatedEmailRequest.withTemplate("OrderDetailsTemplate");

        try {
            templatedEmailRequest.withTemplateData(objectMapper.writeValueAsString(templateData));
        } catch (JsonProcessingException exception) {
            log.error(exception.getMessage());
        }

        templatedEmailRequest.withSource(ApiUtil.getFROM());
        client.sendTemplatedEmail(templatedEmailRequest);
    }

    public String confirmToken(String token) {

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElseThrow(()-> new NoSuchElementException("Invalid token"));

        if (confirmationToken.getConfirmed_at() != null) {
            return ("Your email is already confirmed! Login to Crumbs Food Service to place your first order!");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            confirmationTokenRepository.delete(confirmationToken);
            return ("Your token has expired. Please try signing up again and remember to confirm your email under 15 minutes.");
        }

        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());

        UserDetails user = confirmationToken.getUserDetails();

        UserStatus status = userStatusRepository.findById("REGISTERED").orElse(null);

        if(user.getOwner() != null)
            user.getOwner().setUserStatus(status);
        if(user.getAdmin() != null)
            user.getAdmin().setUserStatus(status);
        if(user.getCustomer() != null)
            user.getCustomer().setUserStatus(status);
        if(user.getDriver() != null)
            user.getDriver().setUserStatus(status);

        return "Your email has successfully been confirmed. You can now login to Crumbs Food Service and start ordering delicious food!";
    }
}