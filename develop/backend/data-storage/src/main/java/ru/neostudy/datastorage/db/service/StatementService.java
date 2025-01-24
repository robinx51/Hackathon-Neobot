package ru.neostudy.datastorage.db.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.db.repository.StatementRepository;

import java.util.List;
import java.util.UUID;

@Service
public class StatementService {
    @Autowired
    public StatementRepository statementRepository;
    private static final Logger logger = LoggerFactory.getLogger(StatementService.class);

    public Statement createStatement(Statement statement) {
        logger.info("Добавление statement в БД");
        return statementRepository.save(statement);
    }

    public void updateStatement(Statement statement) {
        logger.info("Обновление statement с id: {}", statement.getStatementId());
        if (statementRepository.existsById(statement.getStatementId())) {
            statementRepository.save(statement);
            logger.info("Statement обновлён успешно");
        } else {
            logger.error("Statement с id: {} не найден", statement.getStatementId());
        }
    }

    public Statement getStatementById(int statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement с id: " + statementId + " не найден"));
    }

    public List<Statement> getStatementsForUser(User user) {
        return statementRepository.getAllByUserId(user);
    }
}