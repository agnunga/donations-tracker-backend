package io.omosh.dts.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("campaigns")
public class Campaign {
    @Id
    private int id;

    private String goals;
    private LocalDateTime startDate;
    private Double targetAmount;
    private String causes;

}
