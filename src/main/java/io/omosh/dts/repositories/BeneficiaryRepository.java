package io.omosh.dts.repositories;

import io.omosh.dts.models.Beneficiary;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    @Modifying
    @Query("UPDATE Beneficiary b SET b.deletedAt = NULL, b.deletedBy = NULL WHERE b.id = :id")
    void restore(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Beneficiary b SET b.deletedAt = :deletedAt, b.deletedBy = :deletedBy WHERE b.id = :id")
    void softDelete(Long id, LocalDateTime deletedAt, String deletedBy);

    Optional<Beneficiary> findByIdAndDeletedAtIsNull(Long id);

}