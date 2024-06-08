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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component
public class AccountInfo {

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

    public Customer getAccountInfoForFirstCustomer() throws Exception {
        List<String> customerIds = getAccessibleCustomerIds();
        if (!customerIds.isEmpty()) {
            // Extract the customer ID from the resource name
            String customerId = customerIds.get(0).split("/")[1];
            return getAccountInfo(customerId);
        }
        return null;
    }

    public Customer getAccountInfo(String customerId) throws Exception {
        GoogleAdsClient googleAdsClient = createGoogleAdsClient();
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            String query = "SELECT customer.descriptive_name, customer.currency_code, customer.time_zone FROM customer";

            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(customerId)
                    .setQuery(query)
                    .build();

            SearchPagedResponse response = googleAdsServiceClient.search(request);

            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                return googleAdsRow.getCustomer();
            }
        }
        return null;
    }
}
