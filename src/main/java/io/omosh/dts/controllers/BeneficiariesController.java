package io.omosh.dts.controllers;

import io.omosh.dts.models.Beneficiary;
import io.omosh.dts.services.BeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiariesController {

    private final BeneficiaryService beneficiaryService;
    private List<Beneficiary> beneficiaries;
    private static final Logger logger = LoggerFactory.getLogger(BeneficiariesController.class);

    public BeneficiariesController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @PostMapping
    public ResponseEntity<Beneficiary> createBeneficiary(@RequestBody Beneficiary donationDTO) {
        return ResponseEntity.ok(beneficiaryService.saveBeneficiary(donationDTO));
    }

    @GetMapping
    public ResponseEntity<List<Beneficiary>> getAllBeneficiaries(){
        List<Beneficiary> beneficiaries = beneficiaryService.getAllBeneficiaries();
        return ResponseEntity.ok(beneficiaries);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Beneficiary> getBeneficiaryById(@PathVariable Long id) {
        Optional<Beneficiary> donation = beneficiaryService.findById(id);
        return donation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Beneficiary> updateBeneficiary(@PathVariable Long id, @RequestBody Beneficiary updatedBeneficiary) {
        try {
            Beneficiary donation = beneficiaryService.updateBeneficiary(id, updatedBeneficiary);
            return ResponseEntity.ok(updatedBeneficiary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }
    */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id, @RequestParam String deletedBy) {
        Optional<Beneficiary> beneficiary = beneficiaryService.findById(id);
        if (beneficiary.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        beneficiaryService.softDeleteBeneficiary(id, deletedBy);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        beneficiaryService.restore(id);
        return ResponseEntity.noContent().build();
    }

}