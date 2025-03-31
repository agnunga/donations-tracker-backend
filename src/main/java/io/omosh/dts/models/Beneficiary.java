package io.omosh.dts.models;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false) // Ignores superclass fields
@Table(name = "beneficiaries")
@SQLRestriction("deleted_at IS NULL") // Ensures soft-deleted records are excluded globally
public class Beneficiary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contactInfo;
    private String needs;
    private double totalAmount;
    private double disbursedAmount;
    private boolean approvalStatus;


    public double getBalanceAmount() {
        return totalAmount - disbursedAmount;
    }

}
