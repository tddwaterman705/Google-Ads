package com.learning.googleads.api.services;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v17.resources.Customer;
import com.google.ads.googleads.v17.services.CustomerServiceClient;
import com.google.ads.googleads.v17.services.GoogleAdsRow;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v17.services.ListAccessibleCustomersRequest;
import com.google.ads.googleads.v17.services.ListAccessibleCustomersResponse;
import com.google.ads.googleads.v17.services.SearchGoogleAdsRequest;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.learning.googleads.api.auth.GoogleAdsClientFactory;
import com.learning.googleads.api.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AccountInfoService {

    private static final Logger logger = LoggerFactory.getLogger(AccountInfoService.class);

    @Value("${google.ads.developer-token}")
    private String developerToken;

    @Value("${google.ads.login-customer-id:}")
    private String loginCustomerId;

    private final GoogleAdsClientFactory googleAdsClientFactory;
    private GoogleAdsServiceClient googleAdsServiceClient;

    @Autowired
    public AccountInfoService(GoogleAdsClientFactory googleAdsClientFactory) {
        this.googleAdsClientFactory = googleAdsClientFactory;
    }

    private GoogleAdsClient createGoogleAdsClient() throws Exception {
        return googleAdsClientFactory.createGoogleAdsClient();
    }

    private List<String> getAccessibleCustomerIds() throws Exception {
        GoogleAdsClient googleAdsClient = createGoogleAdsClient();
        try (CustomerServiceClient customerServiceClient = googleAdsClient.getLatestVersion().createCustomerServiceClient()) {
            ListAccessibleCustomersRequest request = ListAccessibleCustomersRequest.newBuilder().build();
            ListAccessibleCustomersResponse response = customerServiceClient.listAccessibleCustomers(request);
            return response.getResourceNamesList();
        }
    } 
    

    public CustomerDTO getAccountInfoForFirstCustomer() throws Exception {
        List<String> customerIds = getAccessibleCustomerIds();
        if (!customerIds.isEmpty()) {
            // Extract the customer ID from the resource name
            String customerId = customerIds.get(0).split("/")[1];
            return getAccountInfo(customerId);
        }
        return null;
    }

    public CustomerDTO getAccountInfo(String customerId) throws Exception {
        GoogleAdsClient googleAdsClient = createGoogleAdsClient();
       
        try{
            if(googleAdsServiceClient == null){
            googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
            logger.debug("googleAdsServiceClient created");
            logger.debug("googleAdsServiceClient stored in cache");
            } 
            logger.debug("googleAdsServiceClient retrieved from cache");

            String query = "SELECT customer.id, customer.descriptive_name, customer.currency_code, customer.time_zone, customer.test_account FROM customer";

            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(customerId)
                    .setQuery(query)
                    .build();

            SearchPagedResponse response = googleAdsServiceClient.search(request);

            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                Customer customer = googleAdsRow.getCustomer();
                logger.debug("Customer: {}", customer);
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setCustomerId(Long.toString(customer.getId()));
                customerDTO.setResourceName(customer.getResourceName());
                customerDTO.setDescriptiveName(customer.getDescriptiveName());
                customerDTO.setCurrencyCode(customer.getCurrencyCode());
                customerDTO.setTimeZone(customer.getTimeZone());
                customerDTO.setTestAccount(customer.getTestAccount());
                return customerDTO;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
