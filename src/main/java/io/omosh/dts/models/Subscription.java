package io.omosh.dts.models;

import io.omosh.dts.models.enums.Recurrence;

import java.time.LocalDateTime;

public class Subscription {

    private User user;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Recurrence recurrence;

}
