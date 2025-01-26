package ru.neostudy.apiservice.model;

import lombok.Data;
import ru.neostudy.apiservice.model.enums.StatementStatus;

@Data

public class UpdateStatementDto {
    private int statementId;
    private StatementStatus statementStatus;
}
