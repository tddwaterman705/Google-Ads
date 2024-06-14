package com.learning.googleads.api.web;

import java.time.LocalDate;
import java.util.List;

import com.google.ads.googleads.v17.enums.BiddingStrategyTypeEnum.BiddingStrategyType;
import com.google.ads.googleads.v17.enums.CampaignKeywordMatchTypeEnum.CampaignKeywordMatchType;
import com.google.ads.googleads.v17.enums.CampaignPrimaryStatusEnum.CampaignPrimaryStatus;
import com.google.ads.googleads.v17.enums.CampaignPrimaryStatusReasonEnum.CampaignPrimaryStatusReason;
import com.google.ads.googleads.v17.enums.CampaignServingStatusEnum.CampaignServingStatus;
import com.google.ads.googleads.v17.enums.CampaignStatusEnum.CampaignStatus;

public class CampaignDetailsDTO {

    private Long campaignId;
    private Long customerId;
    private CampaignKeywordMatchType keywordMatchType;
    private String campaignName;
    private Double optimizationScore;
    private CampaignPrimaryStatus primaryStatus;
    private List<CampaignPrimaryStatusReason> primaryStatusReasons;
    private CampaignServingStatus servingStatus;
    private LocalDate startDate;
    private CampaignStatus campaignStatus;
    private String trackingUrl;
    private BiddingStrategyType biddingStrategyType;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public CampaignKeywordMatchType getKeywordMatchType() {
        return keywordMatchType;
    }

    public void setKeywordMatchType(CampaignKeywordMatchType campaignKeywordMatchType) {
        this.keywordMatchType = campaignKeywordMatchType;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Double getOptimizationScore() {
        return optimizationScore;
    }

    public void setOptimizationScore(double optimizationScore) {
        this.optimizationScore = optimizationScore;
    }

    public CampaignPrimaryStatus getPrimaryStatus() {
        return primaryStatus;
    }

    public void setPrimaryStatus(CampaignPrimaryStatus primaryStatus) {
        this.primaryStatus = primaryStatus;
    }

    public List<CampaignPrimaryStatusReason> getPrimaryStatusReasons() {
        return primaryStatusReasons;
    }

    public void setPrimaryStatusReasons(List<CampaignPrimaryStatusReason> primaryStatusReasons) {
        this.primaryStatusReasons = primaryStatusReasons;
    }

    public CampaignServingStatus getServingStatus() {
        return servingStatus;
    }

    public void setServingStatus(CampaignServingStatus servingStatus) {
        this.servingStatus = servingStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = LocalDate.parse(startDate);
    }

    public CampaignStatus getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(CampaignStatus campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public BiddingStrategyType getBiddingStrategyType() {
        return biddingStrategyType;
    }

    public void setBiddingStrategyType(BiddingStrategyType biddingStrategyType2) {
        this.biddingStrategyType = biddingStrategyType2;
    }

    @Override
    public String toString() {
        return "CampaignDetailsDTO: " +
                "\n\tcampaignId=" + campaignId +
                "\n\tcustomerId=" + customerId +
                "\n\tkeywordMatchType='" + keywordMatchType + '\'' +
                "\n\tcampaignName='" + campaignName + '\'' +
                "\n\toptimizationScore=" + optimizationScore +
                "\n\tprimaryStatus='" + primaryStatus + '\'' +
                "\n\tprimaryStatusReasons=" + (primaryStatusReasons != null ? primaryStatusReasons : "[]") +                                                                                    
                "\n\tservingStatus='" + servingStatus + '\'' +
                "\n\tstartDate=" + startDate +
                "\n\tcampaignStatus='" + campaignStatus + '\'' +
                "\n\ttrackingUrl='" + trackingUrl + '\'' +
                "\n\tbiddingStrategyType='" + biddingStrategyType;
    }

}
