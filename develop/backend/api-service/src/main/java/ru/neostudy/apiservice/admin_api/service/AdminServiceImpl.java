package ru.neostudy.apiservice.admin_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neostudy.apiservice.bot.UpdateService;
import ru.neostudy.apiservice.client.interfaces.DataStorageClient;
import ru.neostudy.apiservice.model.ActivePeriod;
import ru.neostudy.apiservice.model.StatementFullDto;
import ru.neostudy.apiservice.model.UpdateStatementDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final DataStorageClient dataStorageClient;
    private final UpdateService updateService;

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
        updateService.setActivePeriod(activePeriod);
    }
}
