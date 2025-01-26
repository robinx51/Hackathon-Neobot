package ru.neostudy.apiservice.admin_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neostudy.apiservice.client.interfaces.DataStorageClient;
import ru.neostudy.apiservice.model.ActivePeriod;
import ru.neostudy.apiservice.model.StatementFullDto;
import ru.neostudy.apiservice.model.UpdateStatementDto;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private ActivePeriod activePeriod;
    private final DataStorageClient dataStorageClient;

    @Override
    public boolean checkIfActivePeriod(LocalDate localDate) {
        if (activePeriod == null) {
            return false;
        }
        return localDate.isEqual(activePeriod.getStartDate()) || localDate.isEqual(activePeriod.getEndDate())
                || (localDate.isAfter(activePeriod.getStartDate()) && localDate.isBefore(activePeriod.getEndDate()));
    }

    @Override
    public void updateStatementStatus(UpdateStatementDto statementDto) throws Exception {
        dataStorageClient.updateStatementStatus(statementDto);
    }

    @Override
    public StatementFullDto getCompleteStatementById(Integer id) throws Exception {
        return dataStorageClient.getCompleteStatementById(id);
    }

    @Override
    public List<StatementFullDto> getCompleteStatements() {
        return dataStorageClient.getCompleteStatements();
    }

    @Override
    public void setActivePeriod(ActivePeriod activePeriod) {
        this.activePeriod = activePeriod;
    }
}
