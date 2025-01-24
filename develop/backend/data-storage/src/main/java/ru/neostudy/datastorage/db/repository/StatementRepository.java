package ru.neostudy.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;

import java.util.List;

@Repository
public interface StatementRepository extends JpaRepository<Statement, Integer> {
    List<Statement> getAllByUserId(User userId);
}