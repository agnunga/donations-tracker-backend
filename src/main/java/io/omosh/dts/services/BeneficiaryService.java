package io.omosh.dts.services;

import io.omosh.dts.models.Beneficiary;
import io.omosh.dts.repositories.BeneficiaryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BeneficiaryService {
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public Flux<Beneficiary> getAllBeneficiaries() {
        return beneficiaryRepository.findAll();
    }

    public Mono<Beneficiary> saveBeneficiary(Beneficiary beneficiary) {
        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(Long id) {
        beneficiaryRepository.deleteById(id);
    }

    public Mono<Beneficiary> findById(Long id) {
        return beneficiaryRepository.findById(id);
    }

    public Mono<Beneficiary> updateBeneficiary(Long id, Beneficiary updatedBeneficiary) {
        return beneficiaryRepository.findById(id)
                .flatMap(existingDonation -> {
                    existingDonation.setTotalAmount(updatedBeneficiary.getTotalAmount()); // Example field update
                    existingDonation.setName(updatedBeneficiary.getName());
                    return beneficiaryRepository.save(existingDonation); // ✅ Now saving the updated record
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Donation not found")));
    }

    public Mono<Void> softDeleteBeneficiary(Long id, String deletedBy) {
        LocalDateTime now = LocalDateTime.now();
//        beneficiaryRepository.softDelete(id, now, deletedBy);
        return Mono.empty();
    }

    public Mono<Void> restore(Long id) {
//        beneficiaryRepository.restore(id);
        return Mono.empty();

    }

}