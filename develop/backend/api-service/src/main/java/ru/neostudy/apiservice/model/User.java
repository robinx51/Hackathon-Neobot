package ru.neostudy.apiservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.apiservice.model.enums.UserRole;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Integer id;
    private Long telegramId;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String phone;
    private UserRole role;
}
