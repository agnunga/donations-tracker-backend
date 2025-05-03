package io.omosh.dts.controllers;

import io.omosh.dts.models.Beneficiary;
import io.omosh.dts.services.BeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public Mono<ResponseEntity<Beneficiary>> createBeneficiary(@RequestBody Beneficiary donationDTO) {
        return beneficiaryService.saveBeneficiary(donationDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty((ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Beneficiary())));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Beneficiary>>> getAllBeneficiaries() {
        return Mono.just(ResponseEntity.ok(beneficiaryService.getAllBeneficiaries()));
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<Beneficiary>> getBeneficiaryById(@PathVariable Long id) {
        return beneficiaryService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new Beneficiary()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Beneficiary>> updateBeneficiary(@PathVariable Long id, @RequestBody Beneficiary updatedBeneficiary) {
        return beneficiaryService.updateBeneficiary(id, updatedBeneficiary)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new Beneficiary()));
    }

    /*
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }
    */

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> softDelete(@PathVariable Long id, @RequestParam String deletedBy) {
        return beneficiaryService.softDeleteBeneficiary(id, deletedBy)
                .thenReturn(ResponseEntity.noContent().build());

    }


    @PutMapping("/{id}/restore")
    public Mono<ResponseEntity<Void>> restore(@PathVariable Long id) {
        return beneficiaryService.restore(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

}