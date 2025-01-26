package ru.neostudy.apiservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.apiservice.bot.Course;
import ru.neostudy.apiservice.model.enums.Role;
import ru.neostudy.apiservice.model.enums.StatementStatus;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementFullDto {
    private int statementId;
    private User user;
    private Course course;
    private StatementStatus statementStatus;
    private Timestamp creationDate;
    private Timestamp changedDate;
    private int userId;
    private Long telegramId;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String phoneNumber;
    private Role role;
}
