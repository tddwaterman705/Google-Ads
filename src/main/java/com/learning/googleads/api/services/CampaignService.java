package com.learning.googleads.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.ads.googleads.v17.resources.BiddingStrategy;
import com.google.ads.googleads.v17.resources.Campaign;
import com.google.ads.googleads.v17.services.GoogleAdsRow;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v17.common.Metrics;
import com.learning.googleads.api.auth.GoogleAdsClientFactory;
import com.learning.googleads.api.web.CampaignDetailsDTO;
import com.learning.googleads.api.web.CampaignMetricsDTO;
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

    public CampaignDetailsDTO getCampaignDetails(String customerId, String campaignId) throws Exception {
        logger.debug("getCampaignDetails called");
        try {
            String query = "SELECT campaign.id, campaign.keyword_match_type, campaign.name, " +
                    "campaign.optimization_score, campaign.primary_status, campaign.primary_status_reasons, " +
                    "campaign.serving_status, campaign.start_date, campaign.status, " +
                    "campaign.tracking_setting.tracking_url, bidding_strategy.type FROM campaign " +
                    "WHERE customer.id = " + customerId + " AND campaign.id = " + campaignId;

            logger.debug(query);

            SearchPagedResponse response = searchGoogleAdsRequestFactory.createCampaignQuery(customerId, query);
            logger.debug("SearchPagedResponse is valid");
            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                Campaign campaign = googleAdsRow.getCampaign();
                BiddingStrategy biddingStrategy = googleAdsRow.getBiddingStrategy();

                CampaignDetailsDTO campaignDetailsDTO = new CampaignDetailsDTO();
                campaignDetailsDTO.setCustomerId(Long.parseLong(customerId));
                campaignDetailsDTO.setCampaignId(campaign.getId());
                campaignDetailsDTO.setKeywordMatchType(campaign.getKeywordMatchType());
                campaignDetailsDTO.setCampaignName(campaign.getName());
                campaignDetailsDTO.setOptimizationScore(campaign.getOptimizationScore());
                campaignDetailsDTO.setPrimaryStatus(campaign.getPrimaryStatus());
                campaignDetailsDTO.setPrimaryStatusReasons(campaign.getPrimaryStatusReasonsList());
                campaignDetailsDTO.setServingStatus(campaign.getServingStatus());
                campaignDetailsDTO.setStartDate(campaign.getStartDate());
                campaignDetailsDTO.setCampaignStatus(campaign.getStatus());
                campaignDetailsDTO.setTrackingUrl(campaign.getTrackingSetting().getTrackingUrl());
                campaignDetailsDTO.setBiddingStrategyType(biddingStrategy.getType());

                logger.debug("CampaignDetailsDTO: {}", campaignDetailsDTO);
                return campaignDetailsDTO;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public CampaignMetricsDTO getCampaignMetrics(String customerId, String campaignId) throws Exception {
        try {
            String query = "SELECT campaign.id, bidding_strategy.type, campaign.name, metrics.all_conversions, " +
                    "metrics.all_conversions_value, metrics.average_cpc, metrics.clicks, metrics.conversions, " +
                    "metrics.conversions_value, metrics.cost_per_conversion, metrics.ctr, metrics.impressions " +
                    "FROM campaign WHERE customer.id = " + customerId + " AND campaign.id = " + campaignId;

            SearchPagedResponse response = searchGoogleAdsRequestFactory.createCampaignQuery(customerId, query);
            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                Campaign campaign = googleAdsRow.getCampaign();
                BiddingStrategy biddingStrategy = googleAdsRow.getBiddingStrategy();
                Metrics metrics = googleAdsRow.getMetrics();

                CampaignMetricsDTO campaignMetricsDTO = new CampaignMetricsDTO();
                campaignMetricsDTO.setCampaignId(Long.parseLong(campaignId));
                campaignMetricsDTO.setBiddingStrategyType(biddingStrategy.getType());
                campaignMetricsDTO.setCampaignName(campaign.getName());
                campaignMetricsDTO.setAllConversions(metrics.getAllConversions());
                campaignMetricsDTO.setAllConversionsValue(metrics.getAllConversionsValue());
                campaignMetricsDTO.setAverageCpc(metrics.getAverageCpc());
                campaignMetricsDTO.setClicks(metrics.getClicks());
                campaignMetricsDTO.setConversions(metrics.getConversions());
                campaignMetricsDTO.setConversionsValue(metrics.getConversionsValue());
                campaignMetricsDTO.setCostPerConversion(metrics.getCostPerConversion());
                campaignMetricsDTO.setCtr(metrics.getCtr());
                campaignMetricsDTO.setImpressions(metrics.getImpressions());
                logger.debug("CampaignMetricsDTO: {}", campaignMetricsDTO);

                return campaignMetricsDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}
