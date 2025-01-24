package ru.neostudy.datastorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.neostudy.datastorage.db.entity.Statement;

@Data
@Builder
public class StatementStatusHistoryDto {
    public enum eChangeType {
        AUTOMATIC, MANUAL
    }

    @Schema(name = "statementStatus", example = "accepted")
    private Statement.eStatementStatus statementStatus;

    @Schema(name = "time", example = "2000-01-01 12:00:00")
    private String time;

    @Schema(name = "changeType", example = "AUTOMATIC")
    private eChangeType changeType;
}