package com.learning.googleads.api.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.learning.googleads.api.services.CampaignService;

@RestController
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/campaigns")

    public ResponseEntity<List<Long>> getAllCmpaignIds(@RequestParam String customerId) {
        try {
            return campaignService.getAllCampaignIds(customerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("campaigns/{campaignId}")

    public CampaignDetailsDTO getCampaignDetails(@RequestParam String customerId, @PathVariable String campaignId) {
        try {
            return campaignService.getCampaignDetails(customerId, campaignId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("campaigns/search/{search}")

    public ResponseEntity<?> queryCampaigns(@RequestParam String customerId, @PathVariable Long campaignId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sort", defaultValue = "name") String sortBy) {

        // TODO: fill out business logic for searching by campaign
        return null;
    }

    @GetMapping("campaigns/metrics")

    public CampaignMetricsDTO getMetrics(@RequestParam String customerId, @RequestParam String campaignId){
        try {
            return campaignService.getCampaignMetrics(customerId, campaignId);

        } catch (Exception e) {
            e.printStackTrace();;
        }
        return null;
    }

}