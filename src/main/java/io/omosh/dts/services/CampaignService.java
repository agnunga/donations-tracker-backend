package io.omosh.dts.services;
import io.omosh.dts.models.Campaign;
import io.omosh.dts.repositories.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Optional<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    public Campaign saveCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }

    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public Campaign updateCampaign(Long id, Campaign updatedCampaign) {
        return campaignRepository.findById(id).map(campaign -> {
            campaign.setCauses(updatedCampaign.getCauses());
            campaign.setGoals(updatedCampaign.getGoals());
            campaign.setTargetAmount(updatedCampaign.getTargetAmount());
            campaign.setStartDate(updatedCampaign.getStartDate());
            return campaignRepository.save(campaign);
        }).orElseThrow(() -> new RuntimeException("Update failed. Campaign not found"));
    }

    public long count() {
        return campaignRepository.count();
    }
}