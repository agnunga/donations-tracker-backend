package io.omosh.dts.models;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false) // Ignores superclass fields
@Table("beneficiaries")
public class Beneficiary extends BaseEntity {
    @Id
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
