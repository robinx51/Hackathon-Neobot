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

@Service
public class StatementService {
    @Autowired
    public StatementRepository statementRepository;
    private static final Logger logger = LoggerFactory.getLogger(StatementService.class);

    public void saveStatement(Statement statement) {
        logger.debug("Добавление statement в БД");
        statementRepository.save(statement);
    }

    public void updateStatement(Statement statement) {
        logger.debug("Обновление statement с id: {}", statement.getStatementId());
        if (statementRepository.existsById(statement.getStatementId())) {
            statementRepository.save(statement);
            logger.debug("Statement обновлён успешно");
        } else {
            logger.error("Statement с id: {} не найден", statement.getStatementId());
        }
    }

    public List<Statement> getStatements() {
        return statementRepository.findAll();
    }

    public Statement getStatementById(int statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement с id: " + statementId + " не найден"));
    }

    public Statement getStatementByUser(User user) {
        return statementRepository.getStatementByUser(user);
    }
}