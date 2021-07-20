package com.example.crumbs.EmailService.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.crumbs.lib.entity.ConfirmationToken;
import com.crumbs.lib.entity.UserDetails;
import com.crumbs.lib.entity.UserStatus;
import com.crumbs.lib.repository.ConfirmationTokenRepository;
import com.crumbs.lib.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(rollbackFor = { Exception.class })
public class EmailService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserStatusRepository userStatusRepository;

    @Autowired
    public EmailService(ConfirmationTokenRepository confirmationTokenRepository, UserStatusRepository userStatusRepository){
        this.confirmationTokenRepository =  confirmationTokenRepository;
        this.userStatusRepository = userStatusRepository;
    }

    private final String accessKey = "AKIAYT66KQD633E5VUSJ";
    private final String secretKey = "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";
    private final String region = "us-east-2";
    public final String from = "crumbsFoodService@gmail.com";

    //    When on group EC2 instance -> configure environment variables:
//    private final String accessKey = ${ACCESS_KEY};
//    private final String secretKey = ${SECRET_KEY};
//    private final String region = ${REGION};
//    public final String from = ${CRUMBS_EMAIL};

    private String emailConfirmationTemplate = "EmailConfirmationTemplate";

    public void sendConfirmationEmail(String email, String name, String token) {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        com.amazonaws.services.simpleemail.AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

        Destination destination = new Destination();
        List<String> toAddresses = new ArrayList<String>();
        toAddresses.add(email);

        destination.setToAddresses(toAddresses);
        SendTemplatedEmailRequest templatedEmailRequest = new SendTemplatedEmailRequest();
        templatedEmailRequest.withDestination(destination);
        templatedEmailRequest.withTemplate(emailConfirmationTemplate);

        String link = "http://localhost:3000/email/verification/" + token;

        String templateData = "{ \"name\":\"" + name + "\", \"link\": \""+ link + "\"}";

        templatedEmailRequest.withTemplateData(templateData);
        templatedEmailRequest.withSource(from);
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

        UserStatus status = userStatusRepository.findById("REGISTERED").get();

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
