package ru.neostudy.apiservice.admin_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private ActivePeriod activePeriod;

    @Override
    public boolean checkIfActivePeriod(LocalDate localDate) {
        if (activePeriod == null) {
            return false;
        }
        return localDate.isEqual(activePeriod.getStartDate()) || localDate.isEqual(activePeriod.getEndDate())
                || (localDate.isAfter(activePeriod.getStartDate()) && localDate.isBefore(activePeriod.getEndDate()));
    }

    @Override
    public void setActivePeriod(ActivePeriod activePeriod) {
        this.activePeriod = activePeriod;
    }
}
