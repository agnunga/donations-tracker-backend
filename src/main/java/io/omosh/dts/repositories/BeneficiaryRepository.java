package io.omosh.dts.repositories;

import io.omosh.dts.models.Beneficiary;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BeneficiaryRepository extends ReactiveCrudRepository<Beneficiary, Long> {

//    @Modifying
//    @Query("UPDATE Beneficiary b SET b.deletedAt = NULL, b.deletedBy = NULL WHERE b.id = :id")
//    void restore(Long id);
//
//    @Transactional
//    @Modifying
//    @Query("UPDATE Beneficiary b SET b.deletedAt = :deletedAt, b.deletedBy = :deletedBy WHERE b.id = :id")
//    void softDelete(Long id, LocalDateTime deletedAt, String deletedBy);
//
//    Optional<Beneficiary> findByIdAndDeletedAtIsNull(Long id);

}