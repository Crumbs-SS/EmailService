package com.example.crumbs.EmailService;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    private String accessKey = "AKIAYT66KQD633E5VUSJ";

    private String secretKey = "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";

    private String region = "us-east-2";

    public String from = "crumbsFoodService@gmail.com";
    //public String[] to = {"crumbsCustomer@gmail.com"};
    private String templateName = "EmailConfirmationTemplate";
    private String templateData;


    public String sendEmail(String email, String name, String token) {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        com.amazonaws.services.simpleemail.AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

        Destination destination = new Destination();
        List<String> toAddresses = new ArrayList<String>();
        toAddresses.add(email);


        destination.setToAddresses(toAddresses);
        SendTemplatedEmailRequest templatedEmailRequest = new SendTemplatedEmailRequest();
        templatedEmailRequest.withDestination(destination);
        templatedEmailRequest.withTemplate(templateName);

        String link = "http://localhost:3000/email/verification/" + token;

        templateData = "{ \"name\":\"" + name + "\", \"link\": \""+ link + "\"}";

        templatedEmailRequest.withTemplateData(templateData);
        templatedEmailRequest.withSource(from);
        client.sendTemplatedEmail(templatedEmailRequest);
        return "email sent";
    }

    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmed_at() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
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

        return "confirmed";
    }


}
