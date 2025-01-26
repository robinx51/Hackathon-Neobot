package ru.neostudy.apiservice.admin_api;

import jakarta.validation.Valid;
import ru.neostudy.apiservice.model.StatementFullDto;
import ru.neostudy.apiservice.model.UpdateStatementDto;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {
    void setActivePeriod(@Valid ActivePeriod activePeriod);

    boolean checkIfActivePeriod(LocalDate localDate);

    void updateStatementStatus(UpdateStatementDto statementDto) throws Exception;

    StatementFullDto getCompleteStatementById(Integer id) throws Exception;

    List<StatementFullDto> getCompleteStatements();

}

