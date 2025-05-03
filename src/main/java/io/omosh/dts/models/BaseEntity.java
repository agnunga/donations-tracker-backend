package io.omosh.dts.models;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {

    private LocalDateTime createdAt;

    private LocalDateTime modifiedOn;

    private String createdBy;

    private String modifiedBy;

    private LocalDateTime deletedAt; // Null means not deleted

    private String deletedBy; // Tracks who deleted the record

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
