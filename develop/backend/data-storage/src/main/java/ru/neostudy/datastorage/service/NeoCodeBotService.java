package ru.neostudy.datastorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neostudy.datastorage.db.entity.Course;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.db.service.CourseService;
import ru.neostudy.datastorage.db.service.StatementService;
import ru.neostudy.datastorage.db.service.UserService;
import ru.neostudy.datastorage.dto.RegistrationDataDto;
import ru.neostudy.datastorage.dto.StatementStatusHistoryDto;
import ru.neostudy.datastorage.dto.StatementsForUserDto;
import ru.neostudy.datastorage.dto.UpdateStatementDto;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NeoCodeBotService {
    private final StatementService statementService;
    private final UserService userService;
    private final CourseService courseService;

    public void newUser(String telegramId) {
        User user = User.builder()
                .telegramId(telegramId)
                .role(User.eRole.visitor)
                .build();
        userService.createUser(user);
    }

    public void registryUser(RegistrationDataDto request) {
        User user = userService.getUserByTelegramId(request.getTelegramId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setCity(request.getCity());
        user.setCity(request.getCity());
        user.setPhoneNumber(request.getPhoneNumber());

        userService.updateUser(user);
    }

    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    public void updateStatement(UpdateStatementDto request) {
        Statement statement = statementService.getStatementById(request.getStatementId());
        statementService.updateStatement(addStatementStatus(statement, request.getStatementStatus(), StatementStatusHistoryDto.eChangeType.MANUAL));
    }

    public void insertCourse(String courseName) {
        Course course = Course.builder()
                .courseName(courseName)
                .build();
        courseService.createCourse(course);
    }

    private Statement addStatementStatus(Statement statement, Statement.eStatementStatus status, StatementStatusHistoryDto.eChangeType changeType) {
        List<StatementStatusHistoryDto> list;
        if (statement.getStatusHistory() == null) {
            list = new ArrayList<>();
            statement.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
            statement.setStatementStatus(status);
        }
        else
            list = statement.getStatusHistory();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StatementStatusHistoryDto statusDto = StatementStatusHistoryDto.builder()
                .statementStatus(status)
                .time(LocalDateTime.now().format(formatter))
                .changeType(StatementStatusHistoryDto.eChangeType.AUTOMATIC)
                .build();
        list.add(statusDto);
        statement.setStatementStatus(status);
        statement.setStatusHistory(list);
        return statement;
    }

    public StatementsForUserDto getStatementsForUser(int userId) {
        User user = userService.getUserById(userId);
        List<Statement> statementList = statementService.getStatementsForUser(user);
        return StatementsForUserDto.builder()
                .user(user)
                .statementList(statementList)
                .build();
    }

    public Optional<User> getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }
}