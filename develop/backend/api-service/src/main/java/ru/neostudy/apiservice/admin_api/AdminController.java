package ru.neostudy.apiservice.admin_api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.neostudy.apiservice.admin_api.service.AdminService;
import ru.neostudy.apiservice.model.ActivePeriod;
import ru.neostudy.apiservice.model.StatementFullDto;
import ru.neostudy.apiservice.model.UpdateStatementDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/set/period")
    @Tag(name = "задание периода подачи заявок",
            description = "админ указывает даты начала и конца периода подачи заявок")
    public void setActivePeriod(@RequestBody @Valid ActivePeriod activePeriod) {
        adminService.setActivePeriod(activePeriod);
    }

    @PutMapping("/statement_status")
    @Tag(name = "Обновление статуса заявки",
            description = "Обновление статуса заявки по statement_id" +
                    " и добавление его в историю через админ панель")
    public void updateStatementStatus(@RequestBody UpdateStatementDto request) throws Exception {
        adminService.updateStatementStatus(request);
    }

    @GetMapping("/statement/{id}")
    @Tag(name = "Просмотр детальной информации о заявке",
            description = "Просмотр детальной информации о заявке с данными пользователя и направлением")
    public StatementFullDto getCompleteStatementById(@PathVariable("id") Integer id) throws Exception {
        return adminService.getCompleteStatementById(id);
    }

    @GetMapping("/statements")
    @Tag(name = "Просмотр списка заявок",
            description = "Просмотр списка заявок с фильтрацией и сортировкой")
    public List<StatementFullDto> getCompleteStatements() throws Exception {
        return adminService.getCompleteStatements();
    }
}
