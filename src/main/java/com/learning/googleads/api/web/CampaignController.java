package com.learning.googleads.api.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.learning.googleads.api.services.CampaignService;

@RestController
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/campaign/info")

    public ResponseEntity<List<Long>> getAllCmpaignIds(@RequestParam String loginCustomerId) {
        try {
            return campaignService.getAllCampaignIds(loginCustomerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
