package ru.neostudy.datastorage.db.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.neostudy.datastorage.db.entity.Statement;
import ru.neostudy.datastorage.db.entity.User;
import ru.neostudy.datastorage.db.repository.StatementRepository;
import ru.neostudy.datastorage.db.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void createUser(User user) {
        logger.debug("Создание user с id: {}", user.getUserId());
        userRepository.save(user);
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

    public User getUserById(int userId) {
        logger.debug("Получение user с id: {}", userId);
        return userRepository.findById(userId)
                .orElseGet(() -> {
                    logger.warn("User с id: {} не найден", userId);
                    throw new EntityNotFoundException("User с id: " + userId + " не найден");
                });
    }

    public List<User> getAllUsers() {
        logger.debug("Получение списка пользователей");
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByTelegramId(String telegramId) {
        Optional<User> optionalUser = userRepository.findByTelegramId(telegramId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            logger.warn("User с telegramId: {} не найден", telegramId);
            throw new EntityNotFoundException("User с telegramId: " + telegramId + " не найден");
        }
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
}