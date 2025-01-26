package ru.neostudy.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.neostudy.datastorage.db.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByTelegramId(Long telegramId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByTelegramId(Long telegramId);

    @Query(value = "SELECT users.user_id, telegram_id, first_name, last_name, city, email, phone_number, role FROM users " +
            "JOIN statements on users.user_id = statements.user_id " +
            "WHERE statements.course_id IS NULL", nativeQuery = true)
    List<User> findAllWithoutCourse();
}