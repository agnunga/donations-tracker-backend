package io.omosh.dts.controllers;

import io.omosh.dts.models.Beneficiary;
import io.omosh.dts.services.BeneficiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiariesController {

    private final BeneficiaryService donationService;
    private List<Beneficiary> beneficiaries;

    public BeneficiariesController(BeneficiaryService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public ResponseEntity<Beneficiary> createBeneficiary(@RequestBody Beneficiary donationDTO) {
        return ResponseEntity.ok(donationService.saveBeneficiary(donationDTO));
    }

    @GetMapping
    public ResponseEntity<List<Beneficiary>> getAllBeneficiaries(){
        List<Beneficiary> beneficiaries = donationService.getAllBeneficiaries();
        return ResponseEntity.ok(beneficiaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beneficiary> getBeneficiaryById(@PathVariable Long id) {
        Optional<Beneficiary> donation = donationService.getBeneficiaryById(id);
        return donation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Beneficiary> updateBeneficiary(@PathVariable Long id, @RequestBody Beneficiary updatedBeneficiary) {
        try {
            Beneficiary donation = donationService.updateBeneficiary(id, updatedBeneficiary);
            return ResponseEntity.ok(updatedBeneficiary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable Long id) {
        donationService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }

}