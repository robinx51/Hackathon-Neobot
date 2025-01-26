package ru.neostudy.datastorage.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.neostudy.datastorage.db.entity.Course;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.dto.StatementFullDto;
import ru.neostudy.datastorage.dto.UpdateStatementDto;
import ru.neostudy.datastorage.dto.UserDto;
import ru.neostudy.datastorage.service.ApiService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@NoArgsConstructor
@AllArgsConstructor
public class BotController {
    @Autowired
    private ApiService apiService;

    @PostMapping("/data-storage/user")
    @Tag(name = "Сохранение пользователя")
    public UserDto saveUser(@RequestBody UserDto request) throws IOException {
        log.debug("Вызов метода user для пользователя с id - {}, email - {}, " +
                "telegramId - {}", request.getId(), request.getEmail(), request.getTelegramUserId());
        return apiService.saveUser(request);
    }

    @GetMapping("/data-storage/users")
    @Tag(name = "Получение списка всех пользователей")
    public List<User> getAllUsers() {
        return apiService.getUsers();
    }

    @GetMapping("/data-storage/users/without_course")
    @Tag(name = "Получение списка всех пользователей")
    public List<User> getAllUsersWithoutCourse() {
        return apiService.getUsersWithoutCourse();
    }

    @GetMapping("/data-storage/user_email/{email}")
    @Tag(name = "Проверка существования почты")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        return apiService.getUser(email);
    }

    @GetMapping("/data-storage/user/{telegramId}")
    @Tag(name = "Проверка существования почты")
    public Optional<User> getUserByTelegramId(@PathVariable Long telegramId) {
        return apiService.getUser(telegramId);
    }

    @GetMapping("/data-storage/getStatements")
    @Tag(name = "Получение всех заявок пользователя")
    public List<Statement> getStatementsForUser() {
        return apiService.getStatements();
    }

    @PutMapping("/data-storage/updateStatementStatus")
    @Tag(name = "Обновление статуса заявки",
            description = "Обновление статуса заявки по statement_id" +
                    " и добавление его в историю через админ панель")
    public void updateStatementStatus(@RequestBody UpdateStatementDto request) {
        apiService.updateStatement(request);
    }

    @GetMapping("/data-storage/courses")
    @Tag(name = "Обновление статуса заявки",
            description = "Обновление статуса заявки по statement_id" +
                    " и добавление его в историю через админ панель")
    public List<Course> getCourses() {
        log.debug("Вызов метода getCourses");
        return apiService.getCourses();
    }

    @GetMapping("/statement/{id}")
    @Tag(name = "Просмотр детальной информации о заявке",
            description = "Просмотр детальной информации о заявке с данными пользователя и направлением")
    public StatementFullDto getCompleteStatementById(@PathVariable("id") Integer id) {
        return apiService.getStatementById(id);
    }

    @PostMapping("/data-storage/insertCourse/{courseName}")
    @Tag(name = "Добавление нового направления обучения")
    public void insertCourse(@PathVariable String courseName) {
        apiService.insertCourse(courseName);
    }
}