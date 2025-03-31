package io.omosh.dts.services;

import io.omosh.dts.models.Beneficiary;
import io.omosh.dts.repositories.BeneficiaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BeneficiaryService {
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public List<Beneficiary> getAllBeneficiaries() {
        return beneficiaryRepository.findAll();
    }

    public Beneficiary saveBeneficiary(Beneficiary beneficiary) {
        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(Long id) {
        beneficiaryRepository.deleteById(id);
    }

    public Optional<Beneficiary> findById(Long id) {
        return beneficiaryRepository.findById(id);
    }

    public Beneficiary updateBeneficiary(Long id, Beneficiary updatedBeneficiary) {
        return beneficiaryRepository.findById(id)
                .map(existingDonation -> {
                    existingDonation.setTotalAmount(updatedBeneficiary.getTotalAmount()); // Example field update
                    existingDonation.setName(updatedBeneficiary.getName());
                    return beneficiaryRepository.save(existingDonation); // ✅ Now saving the updated record
                })
                .orElseThrow(() -> new RuntimeException("Donation not found"));
    }

    @Transactional
    public void softDeleteBeneficiary(Long id, String deletedBy) {
        LocalDateTime now = LocalDateTime.now();
        beneficiaryRepository.softDelete(id, now, deletedBy);
    }

    public void restore(Long id) {
        beneficiaryRepository.restore(id);
    }

}