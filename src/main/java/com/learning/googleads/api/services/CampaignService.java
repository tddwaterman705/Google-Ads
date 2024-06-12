package com.learning.googleads.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.ads.googleads.v17.services.GoogleAdsRow;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.learning.googleads.api.auth.GoogleAdsClientFactory;
import com.learning.googleads.api.web.SearchGoogleAdsRequestFactory;
import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignService {

    private final SearchGoogleAdsRequestFactory searchGoogleAdsRequestFactory;

    private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);

    @Autowired
    public CampaignService(GoogleAdsClientFactory googleAdsClientFactory,
            SearchGoogleAdsRequestFactory searchGoogleAdsRequestFactory) {
        this.searchGoogleAdsRequestFactory = searchGoogleAdsRequestFactory;
    }

    public ResponseEntity<List<Long>> getAllCampaignIds(String customerId) throws Exception {
        try {
            String query = "SELECT campaign.id FROM campaign";
            SearchPagedResponse response = searchGoogleAdsRequestFactory.createCampaignQuery(customerId, query);
            List<Long> campaignIds = new ArrayList<Long>();
            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                Long campaign = googleAdsRow.getCampaign().getId();
                campaignIds.add(campaign);
            }
            logger.debug("campaignIds: " + campaignIds.toString());
            return ResponseEntity.ok(campaignIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
