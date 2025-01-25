package ru.neostudy.apiservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.apiservice.bot.Course;
import ru.neostudy.apiservice.model.enums.UserRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private Long telegramId;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String phone;
    private UserRole role;
    private Course course;
}
