package com.learning.googleads.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v17.services.SearchGoogleAdsRequest;
import com.learning.googleads.api.auth.GoogleAdsClientFactory;

@Component
public class SearchGoogleAdsRequestFactory {

    private GoogleAdsServiceClient googleAdsServiceClient;
    private final GoogleAdsClientFactory googleAdsClientFactory;
    private static final Logger logger = LoggerFactory.getLogger(SearchGoogleAdsRequestFactory.class);

    public SearchGoogleAdsRequestFactory(GoogleAdsClientFactory googleAdsClientFactory) {
        this.googleAdsClientFactory = googleAdsClientFactory;
    }

    public SearchPagedResponse createCampaignQuery(String customerId, String query) {
        try {
            if (googleAdsServiceClient == null) {
                GoogleAdsClient googleAdsClient = googleAdsClientFactory.createGoogleAdsClient();
                googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
                logger.debug("googleAdsServiceClient created");
                logger.debug("googleAdsServiceClient stored in cache");
            }
            logger.debug("googleAdsServiceClient retrieved from cache");

            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(customerId)
                    .setQuery(query)
                    .build();

            SearchPagedResponse response = googleAdsServiceClient.search(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}