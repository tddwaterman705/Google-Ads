package com.learning.googleads.api.controllers;

import com.learning.googleads.api.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.learning.googleads.api.dto.CustomerDTO;

@RestController
public class GoogleAdsController {

    @Autowired
    private CustomerService customerService;

    @Value("${google.ads.login-customer-id:}")
    private String loginCustomerId;

    @Value("${google.ads.time-zone:}")
    private String timeZone;

    @Value("${google.ads.currency-code:}")
    private String currencyCode;

    @GetMapping("/customer/info")
    // Temporarily hardcoding the customer id for testing purposes
    public CustomerDTO getCustomerInfo(/* @RequestParam String customerId */) {
        try {
            return customerService.getCustomerInfo(loginCustomerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/customer/create")

    public CustomerDTO createGoogleAdsCustomer(/*
                                                * @RequestParam String managerId, String timeZone, String currencyCode
                                                */) {
        // Temporarily hardcoding the timeZone, currencyCode, and loginCustomerId
        try {
            return customerService.createGoogleAdsCustomer(timeZone, currencyCode, Long.parseLong(loginCustomerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // @GetMapping("/customerService/first")
    // public CustomerDTO getcustomerServiceForFirstCustomer() {
    // try {
    // return customerService.getcustomerServiceForFirstCustomer();
    // } catch (Exception e) {
    // e.printStackTrace();
    // return null;
    // }
    // }
}
