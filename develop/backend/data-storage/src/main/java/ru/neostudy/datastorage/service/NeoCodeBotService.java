package ru.neostudy.datastorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neostudy.datastorage.db.entity.Course;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.db.service.CourseService;
import ru.neostudy.datastorage.db.service.StatementService;
import ru.neostudy.datastorage.db.service.UserService;
import ru.neostudy.datastorage.dto.UpdateStatementDto;
import ru.neostudy.datastorage.dto.UserDto;
import ru.neostudy.datastorage.enums.StatementStatus;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NeoCodeBotService {
    private final UserService userService;
    private final StatementService statementService;
    private final CourseService courseService;

    public UserDto saveUser(UserDto request) throws IOException {
        User user = User.builder()
                .telegramId(request.getTelegramUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .city(request.getCity())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();

        if (request.getId() != null) {
            user.setUserId(request.getId());
        } else {
            userService.saveUser(user);
            request.setId(user.getUserId());
        }

        Statement statement = statementService.getStatementByUser(user);
        if (statement == null) {
            statement = Statement.builder()
                .user(user)
                .build();
        }
        StatementStatus statementStatus;
        if (request.getCourse() != null)
            statementStatus = StatementStatus.PENDING;
        else
            statementStatus = StatementStatus.PRE_APPLICATION;

        statement.setCourse(request.getCourse());
        statementService.saveStatement(addStatementStatus(statement, statementStatus));

        return request;
    }

    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    public Optional<User> getUser(Long telegramId) {
        return userService.getUser(telegramId);
    }
    public Optional<User> getUser(String email) {
        return userService.getUser(email);
    }

    public List<Statement> getStatements() {
        return statementService.getStatements();
    }

    public void updateStatement(UpdateStatementDto request) {
        Statement statement = statementService.getStatementById(request.getStatementId());
        statementService.updateStatement(addStatementStatus(statement, request.getStatementStatus()));
    }

    public void insertCourse(String courseName) {
        Course course = Course.builder()
                .courseName(courseName)
                .build();
        courseService.createCourse(course);
    }

    public List<Course> getCourses() {
        return courseService.getCourses();
    }

    private Statement addStatementStatus(Statement statement, StatementStatus status) {
        if (statement.getCreationDate() == null) {
            statement.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        statement.setStatementStatus(status);
        statement.setChangedDate(Timestamp.valueOf(LocalDateTime.now()));
        return statement;
    }
}