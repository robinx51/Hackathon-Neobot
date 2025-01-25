package ru.neostudy.apiservice.model.mapper;

import org.springframework.stereotype.Component;
import ru.neostudy.apiservice.model.BotUser;
import ru.neostudy.apiservice.model.UserDto;

@Component
public class UserDtoMapper {
    public UserDto toUserDto(BotUser botUser) {
        return UserDto.builder()
                .telegramId(botUser.getTelegramUserId())
                .firstName(botUser.getFirstName())
                .lastName(botUser.getLastName())
                .city(botUser.getCity())
                .email(botUser.getEmail())
                .phone(botUser.getPhone())
                .role(botUser.getRole())
                .course(botUser.getCourse()) //todo
                .build();
    }
}
