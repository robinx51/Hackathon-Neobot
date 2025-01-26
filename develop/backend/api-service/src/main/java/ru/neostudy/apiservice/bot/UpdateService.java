package ru.neostudy.apiservice.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neostudy.apiservice.bot.enums.ServiceCommand;
import ru.neostudy.apiservice.bot.enums.UserAction;
import ru.neostudy.apiservice.bot.enums.UserState;
import ru.neostudy.apiservice.bot.utils.MessageUtils;
import ru.neostudy.apiservice.client.interfaces.DataStorageClient;
import ru.neostudy.apiservice.model.ActivePeriod;
import ru.neostudy.apiservice.model.BotUser;
import ru.neostudy.apiservice.model.User;
import ru.neostudy.apiservice.model.UserDto;
import ru.neostudy.apiservice.model.enums.Role;
import ru.neostudy.apiservice.model.mapper.UserDtoMapper;
import ru.neostudy.apiservice.model.validation.AppUserValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final AppUserValidator appUserValidator;
    private final UserDtoMapper userDtoMapper;
    private final DataStorageClient dataStorageClient;
    private final Map<String, Course> courses = new HashMap<>();
    private static boolean isActivePeriod = false;
    private static boolean wasNotified = false;
    private final ConcurrentMap<Long, BotUser> users = new ConcurrentHashMap<>();
    private ActivePeriod activePeriod;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        setCourses();
    }

    public void setActivePeriod(ActivePeriod activePeriod) {
        this.activePeriod = activePeriod;
    }

    @Scheduled(fixedRate = 259200000)
    public void checkPeriod() {
        if (activePeriod == null) {
            return;
        }
        LocalDate now = LocalDate.now();
        boolean checkIfActivePeriod = now.isEqual(activePeriod.getStartDate()) || now.isEqual(activePeriod.getEndDate())
                || (now.isAfter(activePeriod.getStartDate()) && now.isBefore(activePeriod.getEndDate()));
        if (checkIfActivePeriod && !wasNotified) {
            sendNotifications();
            wasNotified = true;
            log.info("Период сбора заявок открыт");
        }
        if (!checkIfActivePeriod && isActivePeriod) {
            wasNotified = false;
            log.info("Период сбора заявок завершен");
        }
        isActivePeriod = checkIfActivePeriod;
    }

    private void sendNotifications() {
        List<User> usersList = null;
        try {
            usersList = dataStorageClient.getUsersWithoutCourse();
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
        for (User user : usersList) {
            users.put(user.getTelegramId(), userDtoMapper.toBotUserDto(user));
            sendMessage(new SendMessage(user.getTelegramId().toString(), generateRequest()));
        }
    }

    public void processUpdate(Update update) {
        var output = "";
        if (update == null) {
            log.error("переданный Update равен null");
            return;
        } else if (!update.getMessage().hasText()) {
            log.error("Передано не текстовое сообщение, telegramId = {}", update.getMessage().getFrom().getId());
            output = "Пожалуйста, введите текстовое сообщение согласно инструкции или выберите /help";
        } else {
            String text = update.getMessage().getText();
            Optional<ServiceCommand> serviceCommand = ServiceCommand.fromValue(text);
            if (serviceCommand.isPresent()) { //если пользователь вводит команды
                log.debug("Введена команда {}", serviceCommand);
                output = processServiceCommand(serviceCommand.get(), update);
            } else { //если пользователь вводит просто текст
                output = processUserInput(update);
            }
        }
        SendMessage message = messageUtils.generateSendMessageWithText(update, output);
        sendMessage(message);
    }

    private String processServiceCommand(ServiceCommand serviceCommand, Update update) {
        log.debug("Вызов метода processServiceCommand с командой {}, telegramId = {}", serviceCommand, update.getMessage().getFrom().getId());
        return switch (serviceCommand) {
            case START -> processStartCommand(update);
            case HELP -> processHelpCommand(update);
            case SUBMIT_PREREQUEST -> processSubmitPrerequest(update);
            case SUBMIT_REQUEST -> processSubmitRequest(update);
            case CHOOSE_COURSE -> processChoosingCourse(update);
        };
    }

    private String processSavingCourse(Update update) {
        String output = "";
        String text = update.getMessage().getText();
        Course course = courses.get(text);
        if (course == null) {
            log.error("Указано несуществующее направление: {}", text);
            return "Пожалуйста, укажите направление обучения из списка";
        }
        Optional<User> user;
        Message message = update.getMessage();
        try {
            user = dataStorageClient.getUserByTelegramId(message.getFrom().getId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
        }
        if (user.isPresent()) {
            UserDto userDto = userDtoMapper.fromUsertoUserDto(user.get(), course);
            userDto.setRole(Role.EXTERNAL_USER);
            try {
                log.debug("сохранение пользователя с id {}", userDto.getId());
                dataStorageClient.saveUser(userDto);
                output = String.format("Ваша заявка успешно принята. Для прохождения отборочных испытаний перейдите по ссылке. Используйте почту, " +
                        "указанную в заявке: %s. \n%s", userDto.getEmail(), returnLinkToLogin());
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
        } else {
            return "Пожалуйста, нажмите /start и пройдите регистрацию заново";
        }
        return output;
    }

    private String processUserInput(Update update) {
        Message message = update.getMessage();
        Long telegramId = message.getFrom().getId();
        String text = message.getText();
        BotUser botUser = users.get(telegramId);
        String output = "";

        if (botUser == null) {
            log.error("Пользователя не найдено, telegramId = {}", telegramId);
            return "Пожалуйста, введите /start для начала взаимодействия с ботом";
        }
        UserState userState = botUser.getState();

        switch (userState) {
            case START:
                output = "Пожалуйста, выберите интересующее ваc действие из предложенных или введите /help";
                break;
            case WAIT_FOR_FIRSTNAME:
                boolean nameValid = appUserValidator.isNameValid(text);
                if (!nameValid) {
                    log.error("Некорректно указано имя пользователя: {}", text);
                    return "Пожалуйста, укажите имя кириллицей без пробелов";
                } else {
                    botUser.setFirstName(text);
                    botUser.setState(UserState.WAIT_FOR_LASTNAME);
                    users.put(telegramId, botUser);
                    output = "Введите свою фамилию";
                }
                break;
            case WAIT_FOR_LASTNAME:
                boolean lastNameValid = appUserValidator.isNameValid(text);
                if (!lastNameValid) {
                    log.error("Некорректно указана фамилия пользователя: {}", text);
                    return "Пожалуйста, укажите фамилию кириллицей без пробелов";
                } else {
                    botUser.setLastName(text);
                    botUser.setState(UserState.WAIT_FOR_CITY);
                    users.put(telegramId, botUser);
                    output = "Введите ваш город проживания";
                }
                break;
            case WAIT_FOR_CITY:
                boolean cityValid = appUserValidator.isCityValid(text);
                if (!cityValid) {
                    log.error("Некорректно указан город проживания пользователя: {}", text);
                    return "Пожалуйста, укажите город проживания, используя только буквы русского алфавита";
                } else {
                    botUser.setCity(text);
                    botUser.setState(UserState.WAIT_FOR_PHONE);
                    users.put(telegramId, botUser);
                    output = "Пожалуйста, укажите номер телефона в формате +7xxxxxxxxxx";
                }
                break;
            case WAIT_FOR_PHONE:
                String formattedPhone = appUserValidator.formatPhone(text);
                boolean phoneValid = appUserValidator.isPhoneValid(formattedPhone);
                if (!phoneValid) {
                    log.error("Некорректно указан номер телефона пользователя: {}", formattedPhone);
                    return "Пожалуйста, укажите номер телефона в формате +7xxxxxxxxxx";
                } else {
                    botUser.setPhone(text);
                    botUser.setState(UserState.WAIT_FOR_EMAIL);
                    users.put(telegramId, botUser);
                    output = "Введите ваш адрес электронной почты";
                }
                break;
            case WAIT_FOR_EMAIL:
                boolean emailValid = appUserValidator.isEmailValid(text);
                if (!emailValid) {
                    log.error("Некорректно указана электронная почта пользователя: {}", text);
                    return "Пожалуйста, укажите существующую электронную почту";
                } else {
                    botUser.setEmail(text);
                    botUser.setState(UserState.WAIT_FOR_PHONE);
                    users.put(telegramId, botUser);
                    output = processWaitForEmailStatus(botUser);
                }
                break;
            case WAIT_FOR_COURSE_REQUEST:
                if (courses.get(text) == null) {
                    log.error("Указано несуществующее направление: {}", text);
                    return "Пожалуйста, укажите направление обучения из списка";
                }
                botUser.setCourse(courses.get(text));
                log.debug("course = {}", botUser.getCourse());
                log.debug("course size = {}", courses.size());
                botUser.setRole(Role.CANDIDATE);
                botUser.setState(UserState.COMPLETE);
                users.put(botUser.getTelegramUserId(), botUser);
                output = processWhenSubmitRequest(botUser);
                break;
            case WAIT_FOR_COURSE_NOTIFICATION:
                output = processSavingCourse(update);
                break;
            default:
                output = "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
        return output;
    }

    private String processChoosingCourse(Update update) {
        BotUser botUser = users.get(update.getMessage().getFrom().getId());
        botUser.setState(UserState.WAIT_FOR_COURSE_NOTIFICATION);
        users.put(botUser.getTelegramUserId(), botUser);
        return getCourse();
    }

    private String processWaitForEmailStatus(BotUser botUser) {
        botUser.setState(UserState.COMPLETE);
        botUser.setRole(Role.CANDIDATE);
        users.put(botUser.getTelegramUserId(), botUser);
        String output = "";
        if (botUser.getAction() == UserAction.SUBMIT_PREREQUEST) {
            output = processWhenSubmitPrerequest(botUser);
        } else {
            output = processGettingCourse(botUser);
        }
        return output;
    }

    private String processGettingCourse(BotUser botUser) {
        botUser.setState(UserState.WAIT_FOR_COURSE_REQUEST);
        users.put(botUser.getTelegramUserId(), botUser);
        return getCourse();
    }

    private String getCourse() {
        StringBuilder builder = new StringBuilder();
        for (String s : courses.keySet()) {
            builder.append(s).append("\n");
        }
        return "Пожалуйста, выберите интересующее вас направление\n" +
                builder;
    }

    private String processWhenSubmitRequest(BotUser botUser) {
        String output;
        Optional<User> registeredUserByEmail;
        Optional<User> registeredUserByTgId;

        try {
            registeredUserByEmail = dataStorageClient.getUserByEmail(botUser.getEmail());
            registeredUserByTgId = dataStorageClient.getUserByTelegramId(botUser.getTelegramUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
        }
        output = completeSubmitRequestProcess(botUser, registeredUserByEmail, registeredUserByTgId);
        users.remove(botUser.getTelegramUserId());
        return output;
    }

    private String completeSubmitRequestProcess(BotUser
                                                        botUser, Optional<User> registeredUserByEmail, Optional<User> registeredUserByTgId) {
        String output = "";
        if (registeredUserByEmail.isEmpty() && registeredUserByTgId.isEmpty()) {
            UserDto userDto;
            try {
                userDto = dataStorageClient.saveUser(userDtoMapper.fromBotUsertoUserDto(botUser));
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            log.debug("Пользователь с email {} и telegramId {} сохранен", botUser.getEmail(), botUser.getTelegramUserId());
            userDto.setRole(Role.EXTERNAL_USER);
            try {
                log.debug("сохранение с id = {}", userDto.getId());
                dataStorageClient.saveUser(userDto);
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            output = returnLinkToLogin();
        } else if (registeredUserByEmail.isPresent()) {
            log.debug("Пользователь с email {} уже существует", botUser.getEmail());
            UserDto userDto = userDtoMapper.fromUsertoUserDto(registeredUserByEmail.get(), botUser.getCourse());
            boolean changesPresent = updateFieldsIfApplicable(botUser, userDto);
            if (changesPresent) {
                try {
                    log.debug("вызов метода сохранения пользователя после обновления полей,id = {}", userDto.getId());
                    dataStorageClient.saveUser(userDto);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
                }
            }
            output = String.format("Вы уже регистрировались в системе с данным адресом электронной почты. " +
                    "Для прохождения отборочных испытаний перейдите по ссылке. Используйте почту, " +
                    "указанную в заявке: %s. \nВаша ссылка: %s", userDto.getEmail(), returnLinkToLogin());
        } else if (registeredUserByTgId.isPresent()) {
            log.debug("Пользователь с telegramId {} уже существует", botUser.getTelegramUserId());
            UserDto userDto = userDtoMapper.fromUsertoUserDto(registeredUserByTgId.get(), botUser.getCourse());
            boolean changesPresent = updateFieldsIfApplicable(botUser, userDto);
            if (changesPresent) {
                try {
                    log.debug("вызов метода сохранения пользователя после обновления полей,id = {}", userDto.getId());
                    dataStorageClient.saveUser(userDto);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
                }
            }
            output = String.format("Вы уже регистрировались в системе c данного аккаунта телеграм. " +
                    "Для прохождения отборочных испытаний перейдите по ссылке. Используйте почту, " +
                    "указанную в заявке: %s. \n%s", userDto.getEmail(), returnLinkToLogin());
        }
        return output;
    }

    private String processWhenSubmitPrerequest(BotUser botUser) {
        String output;
        Optional<User> registeredUserByEmail;
        Optional<User> registeredUserByTgId;

        try {
            registeredUserByEmail = dataStorageClient.getUserByEmail(botUser.getEmail());
            registeredUserByTgId = dataStorageClient.getUserByTelegramId(botUser.getTelegramUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
        }
        output = findOrSaveUser(botUser, registeredUserByEmail, registeredUserByTgId);
        users.remove(botUser.getTelegramUserId());
        return output;
    }

    private String findOrSaveUser(BotUser
                                          botUser, Optional<User> registeredUserByEmail, Optional<User> registeredUserByTgId) {
        String output = "";
        if (registeredUserByEmail.isEmpty() && registeredUserByTgId.isEmpty()) {
            try {
                dataStorageClient.saveUser(userDtoMapper.fromBotUsertoUserDto(botUser));
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            log.debug("Пользователь с email {} и telegramId {} сохранен", botUser.getEmail(), botUser.getTelegramUserId());
            output = "Спасибо за регистрацию! Мы уведомим вас, как только начнется прием заявок по направлениям";
        } else if (registeredUserByEmail.isPresent()) {
            log.debug("Пользователь с таким адресом электронной почты уже найден");
            UserDto userDto = userDtoMapper.fromUsertoUserDto(registeredUserByEmail.get(), botUser.getCourse());
            boolean changesPresent = updateFieldsIfApplicable(botUser, userDto);
            if (changesPresent) {
                try {
                    dataStorageClient.saveUser(userDto);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
                }
            }
            output = "Вы уже регистрировались в системе, мы уведомим вас, как только начнется прием заявок по направлениям";
        } else if (registeredUserByTgId.isPresent()) {
            log.debug("Пользователь с таким телеграм id уже найден");
            UserDto userDto = userDtoMapper.fromUsertoUserDto(registeredUserByTgId.get(), botUser.getCourse());
            boolean changesPresent = updateFieldsIfApplicable(botUser, userDto);
            if (changesPresent) {
                try {
                    dataStorageClient.saveUser(userDto);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
                }
            }
            output = "Вы уже регистрировались в системе, мы уведомим вас, как только начнется прием заявок по направлениям";
        }
        return output;
    }

    private boolean updateFieldsIfApplicable(BotUser botUser, UserDto userDto) {
        boolean changesPresent = false;
        if (!botUser.getFirstName().equals(userDto.getFirstName())) {
            log.debug("name changed");
            userDto.setFirstName(botUser.getFirstName());
            changesPresent = true;
        }
        if (!botUser.getLastName().equals(userDto.getLastName())) {
            log.debug("last name changed");
            userDto.setLastName(botUser.getLastName());
            changesPresent = true;
        }
        if (!botUser.getCity().equals(userDto.getCity())) {
            log.debug("city changed");
            userDto.setCity(botUser.getCity());
            changesPresent = true;
        }
        if (!botUser.getPhone().equals(userDto.getPhoneNumber())) {
            log.debug("phone changed");
            userDto.setPhoneNumber(botUser.getPhone());
            changesPresent = true;
        }
        return changesPresent;
    }

    private String processStartCommand(Update update) {
        log.debug("вызов метода processStartCommand, telegramId = {}", update.getMessage().getFrom().getId());
        Long telegramId = update.getMessage().getFrom().getId();
        BotUser user = BotUser.builder()
                .telegramUserId(telegramId)
                .role(Role.VISITOR)
                .state(UserState.START)
                .build();
        users.put(telegramId, user);
        log.debug("Пользователь с телеграм id = {} сохранен", telegramId);
        return getStartMessage();
    }

    private String processHelpCommand(Update update) {
        return help();
    }

    private void sendMessage(SendMessage message) {
        telegramBot.sendAnswerMessage(message);
    }

    private String processSubmitPrerequest(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();
        BotUser botUser = users.get(telegramId);
        if (botUser == null) {
            botUser = BotUser.builder()
                    .telegramUserId(telegramId)
                    .role(Role.VISITOR)
                    .state(UserState.START)
                    .build();
        }
        botUser.setAction(UserAction.SUBMIT_PREREQUEST);
        botUser.setState(UserState.WAIT_FOR_FIRSTNAME);
        users.put(telegramId, botUser);
        return "Введите свое имя";
    }

    private String processSubmitRequest(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();
        BotUser botUser = users.get(telegramId);
        botUser.setAction(UserAction.SUBMIT_REQUEST);
        botUser.setState(UserState.WAIT_FOR_FIRSTNAME);
        users.put(telegramId, botUser);
        return "Введите свое имя";
    }

    private String generateRequest() {
        return "Уважаемый пользователь! Мы рады сообщить, что сегодня открывается набор на стажировку Neoflex. " +
                "Поскольку ранее вы оставляли предзаявку, вам необходимо указать интересующее вас направление обучения\n" +
                "/make_life_choice";
    }

    private String help() {
        if (isActivePeriod) {
            return "Список доступных команд:\n" +
                    "подать заявку(/submit)";
        } else {
            return "Список доступных команд:\n" +
                    "оставить предзаявку(/submit_now)";
        }
    }

    private String getStartMessage() {
        if (isActivePeriod) {
            return "Привет! Добро пожаловать в чат к лучшей версии ассистента Учебного Центра Neoflex (*по версии ее создателей).\n" +
                    "\nДля участия в учебном центре, пожалуйста, выбери следующую команду\n" +
                    "подать заявку(/submit)";
        } else {
            return "Привет! Добро пожаловать в чат к лучшей версии ассистента Учебного Центра Neoflex (*по версии ее создателей).\n" +
                    "\nНа данный момент набор в учебный центр закрыт. Но вы можете оставить предзаявку. Вы получите извещение " +
                    "в этом чате, как только набор снова откроется.\n" +
                    "оставить предзаявку(/submit_now)";
        }
    }

    private String returnLinkToLogin() {
        return "Спасибо за регистрацию! Ваша ссылка: \n" +
                "https://edu.neoflex.ru/";
    }

    public void setCourses() {
        log.debug("вызывается метод setCourses");
        List<Course> currentCourses;
        try {
            currentCourses = dataStorageClient.getCourses();
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
        log.debug("course size = {}", currentCourses.size());
        for (Course course : currentCourses) {
            courses.put(course.getCourseName(), course);
        }
    }
}
