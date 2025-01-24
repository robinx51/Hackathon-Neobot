package ru.neostudy.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neostudy.datastorage.db.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByTelegramId(String telegramId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}