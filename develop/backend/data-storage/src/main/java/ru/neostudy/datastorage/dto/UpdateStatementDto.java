package ru.neostudy.datastorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.neostudy.datastorage.db.entity.Statement;

@Data
@Builder
public class UpdateStatementDto {
    @NotNull
    @Schema(name = "statementId", example = "24")
    private int statementId;

    @NotNull
    @Schema(name = "statementStatus", example = "accepted")
    private Statement.eStatementStatus statementStatus;
}
