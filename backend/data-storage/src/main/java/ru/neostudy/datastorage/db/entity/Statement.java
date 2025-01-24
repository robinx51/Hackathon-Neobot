package ru.neostudy.datastorage.db.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.neostudy.datastorage.dto.StatementStatusHistoryDto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statement")
public class Statement implements Serializable {
    public enum eStatementStatus {
        accepted, rejected, under_consideration
    }

    public enum eCourse {
        analytics, data_engineering, dev_ops_engineer, frontend_developer,
        java_developer, system_engineering, testing
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "statement_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID statementId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User userId;

    @Column(name = "statement_status")
    @Enumerated(EnumType.STRING)
    private eStatementStatus statementStatus;

    @Column(name = "course")
    @Enumerated(EnumType.STRING)
    private eCourse course;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "status_history")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<StatementStatusHistoryDto> statusHistory;
}