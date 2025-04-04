package io.omosh.dts.controllers;

import io.omosh.dts.dtos.DonationsStatsDTO;
import io.omosh.dts.models.Donation;
import io.omosh.dts.services.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;
    private List<Donation> donations;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public ResponseEntity<Donation> createDonation(@RequestBody Donation donationDTO) {
        return ResponseEntity.ok(donationService.saveDonation(donationDTO));
    }

    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations(){
        List<Donation> donations = donationService.getAllDonations();
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonationById(@PathVariable Long id) {
        Optional<Donation> donation = donationService.getDonationsById(id);
        return donation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Donation> updateDonation(@PathVariable Long id, @RequestBody Donation updatedDonation) {
        try {
            Donation donation = donationService.updateDonation(id, updatedDonation);
            return ResponseEntity.ok(updatedDonation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countDonations() {
        long count = donationService.countDonations();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/sum")
    public ResponseEntity<Double> sumDonations() {
        double sum = donationService.sumDonations();
        return ResponseEntity.ok(sum);
    }

    @GetMapping("/stats")
    public ResponseEntity<DonationsStatsDTO> getDonationsStats() {
        DonationsStatsDTO stats = donationService.getDonationsStats();
        return ResponseEntity.ok(stats);
    }

}