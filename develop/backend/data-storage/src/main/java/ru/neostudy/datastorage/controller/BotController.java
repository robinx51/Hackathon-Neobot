package ru.neostudy.datastorage.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.neostudy.datastorage.db.entity.Course;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.dto.UserDto;
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

    @PostMapping("/data-storage/saveUser")
    @Tag(name = "Сохранение пользователя")
    public UserDto saveUser(@RequestBody UserDto request) {
        return neoCodeBotService.saveUser(request);
    }

    @GetMapping("/data-storage/getAllUsers")
    @Tag(name = "Получение списка всех пользователей")
    public List<User> getAllUsers() {
        return neoCodeBotService.getUsers();
    }

    @GetMapping("/data-storage/getUserByEmail/{email}")
    @Tag(name = "Проверка существования почты")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        return neoCodeBotService.getUser(email);
    }

    @GetMapping("/data-storage/getUserByTelegramId/{telegramId}")
    @Tag(name = "Проверка существования почты")
    public Optional<User> getUserByTelegramId(@PathVariable Long telegramId) {
        return neoCodeBotService.getUser(telegramId);
    }

    @GetMapping("/data-storage/getStatements")
    @Tag(name = "Получение всех заявок пользователя")
    public List<Statement> getStatementsForUser() {
        return neoCodeBotService.getStatements();
    }

    @PutMapping("/data-storage/updateStatementStatus")
    @Tag(   name = "Обновление статуса заявки",
            description = "Обновление статуса заявки по statement_id" +
                    " и добавление его в историю через админ панель")
    public void updateStatementStatus(@RequestBody UpdateStatementDto request) {
        neoCodeBotService.updateStatement(request);
    }

    @GetMapping("/data-storage/getCourses")
    @Tag(   name = "Обновление статуса заявки",
            description = "Обновление статуса заявки по statement_id" +
                    " и добавление его в историю через админ панель")
    public List<Course> getCourses() {
        return neoCodeBotService.getCourses();
    }

    @PostMapping("/data-storage/insertCourse/{courseName}")
    @Tag(   name = "Добавление нового направления обучения")
    public void insertCourse(@PathVariable String courseName) {
        neoCodeBotService.insertCourse(courseName);
    }
}