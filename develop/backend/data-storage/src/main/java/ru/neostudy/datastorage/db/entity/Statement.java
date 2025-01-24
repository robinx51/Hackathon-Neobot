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

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statements")
public class Statement implements Serializable {
    public enum eStatementStatus {
        pre_application, accepted, rejected, pending
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statement_id", columnDefinition = "int", updatable = false, nullable = false)
    private int statementId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", updatable = false, nullable = false)
    private Course courseId;

    @Column(name = "statement_status")
    @Enumerated(EnumType.STRING)
    private eStatementStatus statementStatus;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "status_history")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<StatementStatusHistoryDto> statusHistory;
}