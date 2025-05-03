package io.omosh.dts.models;

import io.omosh.dts.models.enums.Recurrence;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Subscription {

    private User user;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Recurrence recurrence;

}
