package ru.neostudy.datastorage.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.datastorage.enums.StatementStatus;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementFullDto {
    private int statementId;
    private String course;
    private StatementStatus statementStatus;
    private Timestamp creationDate;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String phoneNumber;
}
