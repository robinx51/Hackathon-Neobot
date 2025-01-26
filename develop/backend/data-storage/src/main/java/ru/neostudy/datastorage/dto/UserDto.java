package ru.neostudy.datastorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.datastorage.db.entity.Course;
import ru.neostudy.datastorage.enums.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private Long telegramUserId;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String phoneNumber;
    private Role role;
    private Course course;
}