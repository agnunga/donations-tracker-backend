package io.omosh.dts.repositories;

import io.omosh.dts.models.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findByGoals(String goals);
    Optional<Campaign> findByCauses(String email);
}