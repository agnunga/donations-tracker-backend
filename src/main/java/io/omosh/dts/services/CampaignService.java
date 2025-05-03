package io.omosh.dts.services;

import io.omosh.dts.models.Campaign;
import io.omosh.dts.repositories.CampaignRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public Flux<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Mono<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    public Mono<Campaign> saveCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public Mono<Void> deleteCampaign(Long id) {
        return campaignRepository.deleteById(id);
    }

    public Mono<Campaign> createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public Mono<Campaign> updateCampaign(Long id, Campaign updatedCampaign) {
        return campaignRepository.findById(id)
                .flatMap(campaign -> {
                    campaign.setCauses(updatedCampaign.getCauses());
                    campaign.setGoals(updatedCampaign.getGoals());
                    campaign.setTargetAmount(updatedCampaign.getTargetAmount());
                    campaign.setStartDate(updatedCampaign.getStartDate());
                    return campaignRepository.save(campaign);
                }).switchIfEmpty(Mono.error(new RuntimeException("Update failed. Campaign not found")));
    }

    public Mono<Long> count() {
        return campaignRepository.count();
    }
}