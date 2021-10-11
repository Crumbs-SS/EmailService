package com.example.crumbs.EmailService.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.crumbs.lib.entity.Driver;
import com.crumbs.lib.entity.Order;
import com.crumbs.lib.repository.DriverRepository;
import com.crumbs.lib.repository.OrderRepository;
import com.example.crumbs.EmailService.mapper.TemplateDataMapper;
import com.example.crumbs.EmailService.util.ApiUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@Transactional(rollbackFor = Exception.class)
public class SNSService {

    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final AmazonSNS snsClient;

    {
        final String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        final String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        snsClient = AmazonSNSClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_EAST_1).build();
    }


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    SNSService(OrderRepository orderRepository, DriverRepository driverRepository){
        this.driverRepository = driverRepository;
        this.orderRepository = orderRepository;
    }

    public void sendOrderDetailsToPhoneNumber(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow();
        String message = "Order#: " + order.getId() + "\n" + """
        Please visit the link to view your order or make changes:
       
       
        """ + ApiUtil.getClientURL() + "/profile";
        snsClient.publish(new PublishRequest()
                .withMessage("Your order has been placed!\n" + message)
                .withPhoneNumber("+1"+order.getPhone()));
    }

    public void sendOrderRequestToDriverPhone(Long driverId, Long orderId){
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        Driver driver = driverRepository.findById(driverId).orElseThrow();
        Order order = orderRepository.findById(orderId).orElseThrow();
        String driverFirstName = driver.getUserDetails().getFirstName();

        String pickUpLocation = TemplateDataMapper.getAddress(order.getRestaurant().getLocation());
        String dropOffLocation = TemplateDataMapper.getAddress(order.getDeliveryLocation());

        String message = "Hello " + driverFirstName + "!\n " +
                "Order#: " + order.getId() + " is ready for delivery!\n" +
                "Pick up is at " + pickUpLocation + ".\n" +
                "Drop off is at " + dropOffLocation + ".\n" +
                "Estimated pay is: " + dollarFormat.format(order.getDeliveryPay()) + "\n" +
                "Estimated delivery slot is at " + order.getDeliverySlot() + "\n" +
                "To accept this order click the link below: \n" +
                ApiUtil.getClientURL() + "/orders/"+orderId+"/drivers/"+driverId;


        snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber("+1"+driver.getUserDetails().getPhone()));
    }
}
