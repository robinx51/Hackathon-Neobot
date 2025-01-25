package ru.neostudy.datastorage.db.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.db.repository.UserRepository;

import java.io.IOException;
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

    public void updateUser(User user) {
        logger.debug("Обновление user с id: {}", user.getUserId());
        if (userRepository.existsById(user.getUserId())) {
            userRepository.save(user);
        } else {
            logger.warn("User с id: {} не найден", user.getUserId());
            throw new EntityNotFoundException("User с id: " + user.getUserId() + " не найден");
        }
    }

    public List<User> getAllUsers() {
        logger.debug("Получение списка пользователей");
        return userRepository.findAll();
    }

    public Optional<User> getUser(int userId) {
        logger.debug("Получение user с id: {}", userId);
        return userRepository.findById(userId);
    }

    public Optional<User> getUser(String email) {
        logger.debug("Поиск user с email: {}", email);
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUser(Long telegramId) {
        logger.debug("Поиск user с telegramId: {}", telegramId);
        return userRepository.findByTelegramId(telegramId);
    }
}