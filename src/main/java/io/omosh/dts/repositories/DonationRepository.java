package io.omosh.dts.repositories;

import io.omosh.dts.dtos.DonationsStatsDTO;
import io.omosh.dts.models.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    long countByReceived(boolean received);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.donationDate > :startDate and d.donationDate < :endDate")
    long countDonationBetweenDates(@Param("donationDate") LocalDateTime startDate, @Param("donationDate") LocalDateTime endDate);

    List<Donation> findByUserId(Long userId);

    @Query("SELECT sum(d.amount) FROM Donation d")
    double sum();

    @Query("SELECT COUNT(d.amount), SUM(d.amount) FROM Donation d")
    DonationsStatsDTO donationsStats();
}