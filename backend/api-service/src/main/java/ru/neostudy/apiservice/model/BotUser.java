package ru.neostudy.apiservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.apiservice.bot.enums.UserAction;
import ru.neostudy.apiservice.model.enums.UserRole;
import ru.neostudy.apiservice.bot.enums.UserState;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BotUser {
    private Long telegramUserId;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String phone;
    private UserRole role;
    private UserState state;
    private UserAction action;
    private String course; //todo
}
