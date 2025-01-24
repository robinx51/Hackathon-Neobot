package ru.neostudy.datastorage.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.dto.RegistrationDataDto;
import ru.neostudy.datastorage.dto.StatementsForUserDto;
import ru.neostudy.datastorage.dto.UpdateStatementDto;
import ru.neostudy.datastorage.service.NeoCodeBotService;

import java.util.List;
import java.util.Optional;

@RestController
@NoArgsConstructor
@AllArgsConstructor
public class BotController {
    @Autowired
    private NeoCodeBotService neoCodeBotService;

    @PostMapping("/data-storage/createUser/{telegramId}")
    @Tag(name = "Добавление пользователя со статусом visitor")
    public void createUser(@PathVariable String telegramId) {
        neoCodeBotService.newUser(telegramId);
    }

    @PostMapping("/data-storage/insertUser")
    @Tag(name = "Добавление личных данных пользователя")
    public void insertUser(@RequestBody @Validated RegistrationDataDto request) {
        neoCodeBotService.registryUser(request);
    }

    @GetMapping("/data-storage/getUser/{email}")
    @Tag(name = "Проверка существования почты")
    public Optional<User> isEmailExist(@PathVariable String email) {
        return neoCodeBotService.getUserByEmail(email);
    }

    @GetMapping("/data-storage/getAllUsers")
    @Tag(name = "Получение списка всех пользователей")
    public List<User> getAllUsers() {
        return neoCodeBotService.getUsers();
    }

    @GetMapping("/data-storage/getStatementsFor/{userId}")
    @Tag(name = "Получение всех заявок пользователя")
    public StatementsForUserDto getStatementsForUser(@PathVariable int userId) {
        return neoCodeBotService.getStatementsForUser(userId);
    }

    @PutMapping("/data-storage/updateStatement")
    @Tag(   name = "Обновление статуса заявки",
            description = "Обновление статуса заявки по statement_id" +
                    " и добавление его в историю через админ панель")
    public void updateStatement(@RequestBody @Validated UpdateStatementDto request) {
        neoCodeBotService.updateStatement(request);
    }

    @PostMapping("/data-storage/insertCourse/{courseName}")
    @Tag(   name = "Добавление нового направления обучения")
    public void insertCourse(@PathVariable String courseName) {
        neoCodeBotService.insertCourse(courseName);
    }
}