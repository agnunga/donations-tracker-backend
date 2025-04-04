package io.omosh.dts.controllers;

import io.omosh.dts.config.JwtAuthenticationFilter;
import io.omosh.dts.models.Campaign;
import io.omosh.dts.services.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        logger.info("Campaigns PostMapping triggered token ::: {}", "createCampaign");
        return ResponseEntity.ok(campaignService.createCampaign(campaign));
    }

    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns(){
        logger.info("Campaigns GetMapping triggered token ::: {}", "getAllCampaigns");
        List<Campaign> campaigns = campaignService.getAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaignById(@PathVariable Long id) {
        Optional<Campaign> campaign = campaignService.getCampaignById(id);
        return campaign.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Campaign> updateCampaign(@PathVariable Long id, @RequestBody Campaign updatedCampaign) {
        try {
            Campaign campaign = campaignService.updateCampaign(id, updatedCampaign);
            return ResponseEntity.ok(campaign);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long>  countCampaigns() {
        long count = campaignService.count();
        return ResponseEntity.ok(count);
    }

}