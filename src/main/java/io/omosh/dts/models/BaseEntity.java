package io.omosh.dts.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@SQLRestriction("deleted_at IS NULL") // Exclude soft-deleted records globally
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedOn;

    @Column(nullable = false, length = 100)
    private String createdBy;

    @Column(length = 100)
    private String modifiedBy;

    private LocalDateTime deletedAt; // Null means not deleted

    @Column(length = 100)
    private String deletedBy; // Tracks who deleted the record

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
