package ru.neostudy.apiservice.admin_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/set/period")
    public void setActivePeriod(@RequestBody @Valid ActivePeriod activePeriod) {
        adminService.setActivePeriod(activePeriod);
    }
}
