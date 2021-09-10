package com.example.crumbs.EmailService.mapper;

import com.crumbs.lib.entity.FoodOrder;
import com.crumbs.lib.entity.Location;
import com.crumbs.lib.entity.Order;
import com.crumbs.lib.entity.UserDetails;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TemplateDataMapper {

    public static TemplateData orderToTemplateData(Order order){
        return TemplateData.builder()
                .orderId(order.getId())
                .customerAddress(getAddress(order.getDeliveryLocation()))
                .customerEmail(order.getCustomer().getUserDetails().getEmail())
                .customerName(getFullName(order))
                .customerPhone(order.getCustomer().getUserDetails().getPhone())
                .orderCreatedAt(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(order.getCreatedAt()))
                .restaurantAddress(getAddress(order.getRestaurant().getLocation()))
                .restaurantName(order.getRestaurant().getName())
                .orderContents(getOrderContents(order.getFoodOrders()))
                .specialInstructions(order.getPreferences())
                .build();
    }

    public static String getAddress(Location location){
        return location.getStreet() + ", "
                + location.getCity() + ", "
                + location.getState() + " ";
    }

    private static String getFullName(Order order){
        UserDetails userDetails = order.getCustomer().getUserDetails();
        return userDetails.getFirstName() + " " + userDetails.getLastName();
    }

    private static String getOrderContents(List<FoodOrder> foodOrderList){
        ArrayList<String> orderContentsArray = new ArrayList<>();
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));


        orderContentsArray.add("=================================== <br>");

        foodOrderList.forEach(order -> {
            String string = "<p><b>" + order.getMenuItem().getName() +
                    "</b>&emsp; - &emsp;($" + order.getMenuItem().getPrice() + ")" + "</p>"
                    + "<p>" + order.getPreferences() + "</p>";
            orderContentsArray.add(string);
        });

        Float total = foodOrderList.stream()
                .map(order -> order.getMenuItem().getPrice())
                .reduce(0F, Float::sum);
        orderContentsArray.add("=================================== <br> <p>Total Price: " + dollarFormat.format(total));
        return String.join("", orderContentsArray);
    }
}
