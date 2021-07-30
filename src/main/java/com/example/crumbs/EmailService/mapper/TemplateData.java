package com.example.crumbs.EmailService.mapper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateData {
    private String restaurantName;
    private String restaurantAddress;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String customerPhone;
    private Long orderId;
    private String orderCreatedAt;
    private String orderContents;
    private String specialInstructions;
}
