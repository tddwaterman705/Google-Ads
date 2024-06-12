package com.learning.googleads.api.services;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v17.resources.Customer;
import com.google.ads.googleads.v17.services.CreateCustomerClientResponse;
import com.google.ads.googleads.v17.services.CustomerServiceClient;
import com.google.ads.googleads.v17.services.GoogleAdsRow;
import com.google.ads.googleads.v17.services.ListAccessibleCustomersRequest;
import com.google.ads.googleads.v17.services.ListAccessibleCustomersResponse;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.learning.googleads.api.auth.GoogleAdsClientFactory;
import com.learning.googleads.api.web.CustomerDTO;
import com.learning.googleads.api.web.SearchGoogleAdsRequestFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Value("${google.ads.developer-token}")
    private String developerToken;

    @Value("${google.ads.login-customer-id:}")
    private String loginCustomerId;

    private final GoogleAdsClientFactory googleAdsClientFactory;
    private SearchGoogleAdsRequestFactory searchGoogleAdsRequestFactory;

    @Autowired
    public CustomerService(GoogleAdsClientFactory googleAdsClientFactory,
            SearchGoogleAdsRequestFactory searchGoogleAdsRequestFactory) {
        this.googleAdsClientFactory = googleAdsClientFactory;
        this.searchGoogleAdsRequestFactory = searchGoogleAdsRequestFactory;
    }

    private GoogleAdsClient createGoogleAdsClient() throws Exception {
        return googleAdsClientFactory.createGoogleAdsClient();
    }

    private List<String> getAccessibleCustomerIds() throws Exception {
        GoogleAdsClient googleAdsClient = createGoogleAdsClient();
        try (CustomerServiceClient customerServiceClient = googleAdsClient.getLatestVersion()
                .createCustomerServiceClient()) {
            ListAccessibleCustomersRequest request = ListAccessibleCustomersRequest.newBuilder().build();
            ListAccessibleCustomersResponse response = customerServiceClient.listAccessibleCustomers(request);
            return response.getResourceNamesList();
        }
    }

    public CustomerDTO getCustomerInfoForFirstCustomer() throws Exception {
        List<String> customerIds = getAccessibleCustomerIds();
        if (!customerIds.isEmpty()) {
            // Extract the customer ID from the resource name
            String customerId = customerIds.get(0).split("/")[1];
            return getCustomerInfo(customerId);
        }
        return null;
    }

    public CustomerDTO getCustomerInfo(String customerId) throws Exception {

        try {
            String query = "SELECT customer.id, customer.descriptive_name, customer.currency_code, customer.time_zone, customer.test_account FROM customer";
            SearchPagedResponse response = searchGoogleAdsRequestFactory.createCampaignQuery(customerId, query);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CustomerDTO createGoogleAdsCustomer(String timeZone, String currencyCode, Long managerId, String accountName)
            throws Exception {
        String dateTime = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        // Initializes a Customer object to be created.
        Customer newCustomer = Customer.newBuilder()
                .setDescriptiveName(accountName + ". Account created on '" + dateTime + "'")
                .setCurrencyCode(currencyCode)
                .setTimeZone(timeZone)
                .build();

        GoogleAdsClient googleAdsClient = createGoogleAdsClient();
        try (CustomerServiceClient client = googleAdsClient.getLatestVersion().createCustomerServiceClient()) {
            CreateCustomerClientResponse response = client.createCustomerClient(managerId.toString(), newCustomer);
            logger.debug(
                    "Created a customer with resource name " + response.getResourceName()
                            + " under the manager account " + managerId);

            CustomerDTO customer = new CustomerDTO();
            customer.setCustomerId(response.getResourceName().split("/")[1]);
            customer.setCurrencyCode(newCustomer.getCurrencyCode());
            customer.setDescriptiveName(newCustomer.getDescriptiveName());
            customer.setTimeZone(newCustomer.getTimeZone());
            customer.setResourceName(response.getResourceName());
            return customer;
        }
    }
}
