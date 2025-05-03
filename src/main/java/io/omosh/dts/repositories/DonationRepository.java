package io.omosh.dts.repositories;

import io.omosh.dts.models.Donation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DonationRepository extends ReactiveCrudRepository<Donation, Long> {

//    long countByReceived(boolean received);
//
//    @Query("SELECT COUNT(d) FROM Donation d WHERE d.donationDate > :startDate and d.donationDate < :endDate")
//    long countDonationBetweenDates(@Param("donationDate") LocalDateTime startDate, @Param("donationDate") LocalDateTime endDate);

//    Flux<Donation> findByUserId(Long userId);

//    @Query("SELECT sum(d.amount) FROM Donation d")
//    double sum();

//    @Query("SELECT COUNT(d.amount), SUM(d.amount) FROM Donation d")
//    DonationsStatsDTO donationsStats();
}