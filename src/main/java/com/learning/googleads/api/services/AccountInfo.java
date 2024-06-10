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
import com.google.api.client.auth.oauth2.Credential;
import com.google.auth.oauth2.UserCredentials;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.learning.googleads.api.auth.OAuthService;
import com.learning.googleads.api.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class AccountInfo {

    private static final Logger logger = LoggerFactory.getLogger(AccountInfo.class);

    @Value("${google.ads.developer-token}")
    private String developerToken;

    @Value("${google.ads.login-customer-id:}")
    private String loginCustomerId;

    private final OAuthService oAuthService;

    @Autowired
    public AccountInfo(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    private GoogleAdsClient createGoogleAdsClient() throws Exception {
        Credential credential = oAuthService.getCredential();
        GoogleClientSecrets clientSecrets = oAuthService.getClientSecrets();

        UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(clientSecrets.getDetails().getClientId())
                .setClientSecret(clientSecrets.getDetails().getClientSecret())
                .setRefreshToken(credential.getRefreshToken())
                .build();

        GoogleAdsClient.Builder builder = GoogleAdsClient.newBuilder()
                .setCredentials(userCredentials)
                .setDeveloperToken(developerToken);

        if (loginCustomerId != null && !loginCustomerId.isEmpty()) {
            builder.setLoginCustomerId(Long.parseLong(loginCustomerId));
        }

        return builder.build();
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
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
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
        return null;
    }
}
