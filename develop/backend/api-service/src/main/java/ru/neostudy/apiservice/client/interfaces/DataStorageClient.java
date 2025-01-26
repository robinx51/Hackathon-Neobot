package ru.neostudy.apiservice.client.interfaces;

import ru.neostudy.apiservice.bot.Course;
import ru.neostudy.apiservice.model.StatementFullDto;
import ru.neostudy.apiservice.model.UpdateStatementDto;
import ru.neostudy.apiservice.model.User;
import ru.neostudy.apiservice.model.UserDto;

import java.util.List;
import java.util.Optional;

public interface DataStorageClient {
    Optional<User> getUserByEmail(String email) throws Exception;

    Optional<User> getUserByTelegramId(Long telegramId) throws Exception;

    UserDto saveUser(UserDto userDto) throws Exception;

    List<Course> getCourses() throws Exception;

    List<User> getUsersWithoutCourse() throws Exception;

    void updateStatementStatus(UpdateStatementDto statementDto) throws Exception;

    StatementFullDto getCompleteStatementById(Integer id) throws Exception;
}