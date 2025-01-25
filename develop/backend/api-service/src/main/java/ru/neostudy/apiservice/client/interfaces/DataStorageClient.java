package ru.neostudy.apiservice.client.interfaces;

import ru.neostudy.apiservice.model.UserDto;

import java.util.List;
import java.util.Optional;

public interface DataStorageClient {
    Optional<UserDto> getUserByEmail(String email) throws Exception;
    Optional<UserDto> getUserByTelegramId(Long telegramId) throws Exception;
    UserDto saveUser(UserDto userDto) throws Exception;
    List<String> getCourses() throws Exception;
}
