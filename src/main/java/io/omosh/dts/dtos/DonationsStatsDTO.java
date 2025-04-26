package io.omosh.dts.dtos;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceCreator;

@Data
public class DonationsStatsDTO {

    private Long donationCount;
    private Double totalAmount;

    @PersistenceCreator
    public DonationsStatsDTO(Long donationCount, Double totalAmount) {
        this.donationCount = donationCount;
        this.totalAmount = totalAmount;
    }

}