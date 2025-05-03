package io.omosh.dts.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("donations")
public class Donation {
    @Id
    private Long id;

    private BigDecimal amount;
    private String donorName;
    private String details;
    private boolean received;
    private LocalDateTime donationDate;

    private User user;

    private Campaign campaign;

}