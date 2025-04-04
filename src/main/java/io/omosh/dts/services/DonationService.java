package io.omosh.dts.services;
import io.omosh.dts.dtos.DonationsStatsDTO;
import io.omosh.dts.models.Donation;
import io.omosh.dts.repositories.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {
    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public List<Donation> getDonationsByUser(Long userId) {
        return donationRepository.findByUserId(userId);
    }

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }

    public Optional<Donation> getDonationsById(Long id) {
        return donationRepository.findById(id);
    }

    public long getDonationsCount() {
        return donationRepository.count();
    }

    public Donation updateDonation(Long id, Donation updatedDonation) {
        return donationRepository.findById(id)
                .map(existingDonation -> {
                    existingDonation.setAmount(updatedDonation.getAmount()); // Example field update
                    existingDonation.setDonorName(updatedDonation.getDonorName());
                    return donationRepository.save(existingDonation); // ✅ Now saving the updated record
                })
                .orElseThrow(() -> new RuntimeException("Donation not found"));
    }

    public long countDonations() {
        return donationRepository.count();
    }

    public double sumDonations() {
        return donationRepository.sum();
    }

    public DonationsStatsDTO getDonationsStats() {
        //return donationRepository.donationsStats();
        return new DonationsStatsDTO(donationRepository.count(), donationRepository.sum());
    }

}
