package ru.neostudy.datastorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatementsForUserDto {
    private User user;

    private List<Statement> statementList;
}
