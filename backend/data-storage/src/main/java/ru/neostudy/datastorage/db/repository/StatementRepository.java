package ru.neostudy.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neostudy.datastorage.db.entity.Statement;

import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
}