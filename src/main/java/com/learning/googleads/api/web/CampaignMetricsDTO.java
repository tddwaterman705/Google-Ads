package com.learning.googleads.api.web;

import com.google.ads.googleads.v17.enums.BiddingStrategyTypeEnum.BiddingStrategyType;

public class CampaignMetricsDTO {
    private Long campaignId;
    private BiddingStrategyType biddingStrategyType;
    private String campaignName;
    private Double allConversions;
    private Double allConversionsValue;
    private Double averageCpc;
    private Long clicks;
    private Double conversions;
    private Double conversionsValue;
    private Double costPerConversion;
    private Double ctr;
    private Long impressions;

    // Getters
    public Long getCampaignId() {
        return campaignId;
    }

    public BiddingStrategyType getBiddingStrategyType() {
        return biddingStrategyType;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public Double getAllConversions() {
        return allConversions;
    }

    public Double getAllConversionsValue() {
        return allConversionsValue;
    }

    public Double getAverageCpc() {
        return averageCpc;
    }

    public Long getClicks() {
        return clicks;
    }

    public Double getConversions() {
        return conversions;
    }

    public Double getConversionsValue() {
        return conversionsValue;
    }

    public Double getCostPerConversion() {
        return costPerConversion;
    }

    public Double getCtr() {
        return ctr;
    }

    public Long getImpressions() {
        return impressions;
    }

    // Setters
    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public void setBiddingStrategyType(BiddingStrategyType biddingStrategyType) {
        this.biddingStrategyType = biddingStrategyType;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void setAllConversions(Double allConversions) {
        this.allConversions = allConversions;
    }

    public void setAllConversionsValue(Double allConversionsValue) {
        this.allConversionsValue = allConversionsValue;
    }

    public void setAverageCpc(Double averageCpc) {
        this.averageCpc = averageCpc;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public void setConversions(Double conversions) {
        this.conversions = conversions;
    }

    public void setConversionsValue(Double conversionsValue) {
        this.conversionsValue = conversionsValue;
    }

    public void setCostPerConversion(Double costPerConversion) {
        this.costPerConversion = costPerConversion;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    public void setImpressions(Long impressions) {
        this.impressions = impressions;
    }

    @Override
    public String toString() {
        return "CampaignMetricsDTO: " +
                "\n\tcampaignId=" + campaignId +
                "\n\tbiddingStrategyType='" + biddingStrategyType + '\'' +
                "\n\tcampaignName='" + campaignName + '\'' +
                "\n\tallConversions=" + allConversions +
                "\n\tallConversionsValue=" + allConversionsValue +
                "\n\taverageCpc=" + averageCpc +
                "\n\tclicks=" + clicks +
                "\n\tconversions=" + conversions +
                "\n\tconversionsValue=" + conversionsValue +
                "\n\tcostPerConversion=" + costPerConversion +
                "\n\tctr=" + ctr +
                "\n\timpressions=" + impressions;
    }
}
