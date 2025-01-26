package ru.neostudy.apiservice.admin_api;

import jakarta.validation.Valid;

import java.time.LocalDate;

public interface AdminService {
    void setActivePeriod(@Valid ActivePeriod activePeriod);

    boolean checkIfActivePeriod(LocalDate localDate);
}
