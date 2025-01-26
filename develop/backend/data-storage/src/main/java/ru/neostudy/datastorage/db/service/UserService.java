package ru.neostudy.datastorage.db.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.db.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User saveUser(User user) {
        logger.debug("Сохранение user с id: {}", user.getUserId());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        logger.debug("Получение списка пользователей");
        return userRepository.findAll();
    }

    public List<User> getUsersWithoutCourse() {
        logger.debug("Получение пользователей без выбранного курса");
        List<User> allWithoutCourse = userRepository.findAllWithoutCourse();
        logger.debug("usersWithoutCourse size = {}", allWithoutCourse.size());
        return allWithoutCourse;
    }

    public Optional<User> getUser(String email) {
        logger.debug("Поиск user с email: {}", email);
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUser(Long telegramId) {
        logger.debug("Поиск user с telegramId: {}", telegramId);
        return userRepository.findByTelegramId(telegramId);
    }}