package io.omosh.donations.services;
import io.omosh.donations.models.Donation;
import io.omosh.donations.repositories.DonationRepository;
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

    public Donation updateDonation(Long id, Donation updatedDonation) {
        return donationRepository
                .findById(id).map(donationRepository::save)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
