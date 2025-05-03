package io.omosh.dts.repositories;

import io.omosh.dts.models.Campaign;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CampaignRepository extends ReactiveCrudRepository<Campaign, Long> {

}