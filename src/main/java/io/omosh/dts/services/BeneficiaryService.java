package io.omosh.dts.services;

import io.omosh.dts.models.Beneficiary;
import io.omosh.dts.repositories.BeneficiaryRepository;
import org.springframework.stereotype.Service;

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

    public Optional<Beneficiary> getBeneficiaryById(Long id) {
        return beneficiaryRepository.findById(id);
    }

    public Beneficiary updateBeneficiary(Long id, Beneficiary updatedBeneficiary) {
        return beneficiaryRepository.findById(id)
                .map(existingDonation -> {
                    existingDonation.setAmount(updatedBeneficiary.getAmount()); // Example field update
                    existingDonation.setName(updatedBeneficiary.getName());
                    return beneficiaryRepository.save(existingDonation); // ✅ Now saving the updated record
                })
                .orElseThrow(() -> new RuntimeException("Donation not found"));
    }

}