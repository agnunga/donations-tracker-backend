package io.omosh.dts.controllers;

import io.omosh.dts.dtos.DonationsStatsDTO;
import io.omosh.dts.models.Donation;
import io.omosh.dts.services.DonationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;
    private List<Donation> donations;
    private final Logger logger = LoggerFactory.getLogger(DonationController.class);

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public Mono<ResponseEntity<Donation>> createDonation(@RequestBody Donation donationDTO) {
        return donationService.saveDonation(donationDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Donation>>> getAllDonations() {
        return Mono.just(ResponseEntity.ok(donationService.getAllDonations()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Donation>> getDonationById(@PathVariable Long id) {
        return donationService.getDonationsById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Donation>> updateDonation(@PathVariable Long id, @RequestBody Donation updatedDonation) {
        return donationService.updateDonation(id, updatedDonation)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDonation(@PathVariable Long id) {
        return donationService.deleteDonation(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countDonations() {
        return donationService.countDonations()
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @GetMapping("/sum")
    public Mono<ResponseEntity<Double>> sumDonations() {
        return donationService.sumDonations()
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

    @GetMapping("/stats")
    public Mono<ResponseEntity<DonationsStatsDTO>> getDonationsStats() {
        return donationService.getDonationsStats()
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null)));
    }

}