package io.omosh.dts.controllers;

import io.omosh.dts.models.Campaign;
import io.omosh.dts.services.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private static final Logger logger = LoggerFactory.getLogger(CampaignController.class);

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public Mono<ResponseEntity<Campaign>> createCampaign(@RequestBody Campaign campaign) {
        logger.info("Campaigns PostMapping triggered token ::: {}", "createCampaign");
        return campaignService.createCampaign(campaign)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Campaign>>> getAllCampaigns() {
        logger.info("Campaigns GetMapping triggered token ::: {}", "getAllCampaigns");
        return Mono.just(ResponseEntity.ok(campaignService.getAllCampaigns()));

    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Campaign>> getCampaignById(@PathVariable Long id) {
        return campaignService.getCampaignById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Campaign>> updateCampaign(@PathVariable Long id, @RequestBody Campaign updatedCampaign) {
        return campaignService.updateCampaign(id, updatedCampaign)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCampaign(@PathVariable Long id) {
        return campaignService.deleteCampaign(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countCampaigns() {
        return campaignService.count()
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

}