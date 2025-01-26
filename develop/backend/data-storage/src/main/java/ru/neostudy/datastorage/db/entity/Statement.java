package ru.neostudy.datastorage.db.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neostudy.datastorage.enums.StatementStatus;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statements")
public class Statement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statement_id", columnDefinition = "int", updatable = false, nullable = false)
    private int statementId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "statement_status")
    @Enumerated(EnumType.STRING)
    private StatementStatus statementStatus;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "changed_date")
    private Timestamp changedDate;
}