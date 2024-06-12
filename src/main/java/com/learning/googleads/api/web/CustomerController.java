package com.learning.googleads.api.web;

import com.learning.googleads.api.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer/info")
    
    public CustomerDTO getCustomerInfo(@RequestParam String loginCustomerId) {
        try {
            return customerService.getCustomerInfo(loginCustomerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/customer/create")

    public CustomerDTO createGoogleAdsCustomer(@RequestParam String managerId, String accountName, String timeZone, String currencyCode) {
        try {
            return customerService.createGoogleAdsCustomer(timeZone, currencyCode, Long.parseLong(managerId), accountName);
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
