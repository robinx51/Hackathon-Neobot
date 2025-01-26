package ru.neostudy.apiservice.admin_api;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivePeriod {
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private LocalDate endDate;
}
