package com.learning.googleads.api.controllers;

import com.learning.googleads.api.services.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.learning.googleads.api.dto.CustomerDTO;

@RestController
public class GoogleAdsController {

    @Autowired
    private AccountInfoService accountInfo;

    @Value("${google.ads.login-customer-id:}")
    private String loginCustomerId;

    @GetMapping("/accountInfo")
    // Temporarily hardcoding the customer id for testing purposes
    public CustomerDTO getAccountInfo(/* @RequestParam String customerId */) {
        try {
            return accountInfo.getAccountInfo(loginCustomerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // @GetMapping("/accountInfo/first")
    // public CustomerDTO getAccountInfoForFirstCustomer() {
    // try {
    // return accountInfo.getAccountInfoForFirstCustomer();
    // } catch (Exception e) {
    // e.printStackTrace();
    // return null;
    // }
    // }
}
