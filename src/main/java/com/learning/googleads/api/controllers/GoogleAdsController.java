package com.learning.googleads.api.controllers;

import com.google.ads.googleads.v17.resources.Customer;
import com.learning.googleads.api.services.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAdsController {

    @Autowired
    private AccountInfo accountInfo;

    @GetMapping("/accountInfo")
    public Customer getAccountInfo(@RequestParam String customerId) {
        try {
            return accountInfo.getAccountInfo(customerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/accountInfo/first")
    public Customer getAccountInfoForFirstCustomer() {
        try {
            return accountInfo.getAccountInfoForFirstCustomer();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
