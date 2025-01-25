package ru.neostudy.apiservice.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neostudy.apiservice.bot.enums.ServiceCommand;
import ru.neostudy.apiservice.bot.enums.UserAction;
import ru.neostudy.apiservice.bot.enums.UserState;
import ru.neostudy.apiservice.bot.utils.MessageUtils;
import ru.neostudy.apiservice.client.interfaces.DataStorageClient;
import ru.neostudy.apiservice.model.BotUser;
import ru.neostudy.apiservice.model.UserDto;
import ru.neostudy.apiservice.model.enums.UserRole;
import ru.neostudy.apiservice.model.mapper.UserDtoMapper;
import ru.neostudy.apiservice.model.validation.AppUserValidator;

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
    private final Map<String, Object> courses = new HashMap<>(); //ConcurrentMap?

    private final ConcurrentMap<Long, BotUser> users = new ConcurrentHashMap<>();
    //todo добавить логику по очистке юзера при длительном молчании

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
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
            case CANCEL -> processCancelCommand(update); //todo удалить если не надо
        };
    }

    private String processCancelCommand(Update update) {
        users.remove(update.getMessage().getFrom().getId());
        return "Переданные вами ранее данные были удалены, пожалуйста,";
    }

    private void sendMessage(SendMessage message) {
        telegramBot.sendAnswerMessage(message);
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
                    output = "Введите ваш номер телефона в формате +7xxxxxxxxxx";
                }
                break;
            case WAIT_FOR_PHONE:
                String formattedPhone = appUserValidator.formatPhone(text);
                boolean phoneValid = appUserValidator.isPhoneValid(formattedPhone);
                if (!phoneValid) {
                    log.error("Некорректно указан номер телефона пользователя: {}", formattedPhone);
                    return "Пожалуйста, укажите номер телефона в формате +7xxxxxxxxxx";
                } else {
                    output = processWaitForPhoneStatus(formattedPhone, botUser);
                }
                break;
            case WAIT_FOR_COURSE:
                if (courses.containsKey(text)) {
                    log.error("Указано несуществующее направление: {}", text);
                    return "Пожалуйста, укажите направление обучения из списка";
                }
                botUser.setCourse(text);
                botUser.setRole(UserRole.CANDIDATE);
                botUser.setState(UserState.COMPLETE);
                users.put(botUser.getTelegramUserId(), botUser);
                output = processWhenSubmitRequest(botUser);
                break;
            default:
                output = "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
        return output;
    }

    private String processWaitForPhoneStatus(String phone, BotUser botUser) {
        botUser.setPhone(phone);
        botUser.setState(UserState.COMPLETE);
        botUser.setRole(UserRole.CANDIDATE); //меняется роль после ввода всех данных
        users.put(botUser.getTelegramUserId(), botUser);
        String output = "";
        if (botUser.getAction() == UserAction.SUBMIT_PREREQUEST) {
            output = processWhenSubmitPrerequest(botUser);
        } else {
            output = suggestCourses(botUser);
        }
        return output;
    }

    private String suggestCourses(BotUser botUser) {
        botUser.setState(UserState.WAIT_FOR_COURSE);
        StringBuilder builder = new StringBuilder();
        for (String s : courses.keySet()) {
            builder.append("/").append(s).append("\n");
        }
        return "Пожалуйста, выберите интересующее вас направление\n" +
                "Java Development" +
                builder; //todo дополнить направления
    }

    private String processWhenSubmitRequest(BotUser botUser) {
        String output;
        Optional<UserDto> registeredUserByEmail;
        Optional<UserDto> registeredUserByTgId;

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

    private String completeSubmitRequestProcess(BotUser botUser, Optional<UserDto> registeredUserByEmail, Optional<UserDto> registeredUserByTgId) {
        String output;
        if (registeredUserByEmail.isEmpty() && registeredUserByTgId.isEmpty()) {
            UserDto userDto;
            try {
                userDto = dataStorageClient.saveUser(userDtoMapper.toUserDto(botUser));
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            log.debug("Пользователь с email {} и telegramId {} сохранен", botUser.getEmail(), botUser.getTelegramUserId());
            userDto.setRole(UserRole.EXTERNAL_USER);
            try {
                //todo update
                dataStorageClient.saveUser(userDto);
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            output = returnLinkToLogin();
        } else if (registeredUserByEmail.isPresent()) {
            log.debug("Пользователь с email {} уже существует", botUser.getEmail());
            UserDto userDto = registeredUserByEmail.get();
            updateFieldsIfApplicable(botUser, userDto);
            try {
                //todo update
                dataStorageClient.saveUser(userDto);
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            output = String.format("Вы уже регистрировались в системе с данным адресом электронной почты. " +
                    "Для прохождения отборочных испытаний перейдите по ссылке. Используйте почту, " +
                    "указанную в заявке: %s. \nВаша ссылка: %s", userDto.getEmail(), returnLinkToLogin());
        } else {
            log.debug("Пользователь с telegramId {} уже существует", botUser.getTelegramUserId());
            UserDto userDto = registeredUserByTgId.get();
            updateFieldsIfApplicable(botUser, userDto);
            try {
                //todo update
                dataStorageClient.saveUser(userDto);
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            output = String.format("Вы уже регистрировались в системе c данного аккаунта телеграм. " +
                    "Для прохождения отборочных испытаний перейдите по ссылке. Используйте почту, " +
                    "указанную в заявке: %s. \nВаша ссылка: %s", userDto.getEmail(), returnLinkToLogin());
        }
        return output;
    }

    private String returnLinkToLogin() {
        return "Спасибо за регистрацию! Ваша ссылка ..."; //todo
    }

    private String processWhenSubmitPrerequest(BotUser botUser) {
        String output;
        Optional<UserDto> registeredUserByEmail;
        Optional<UserDto> registeredUserByTgId;

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

    private String findOrSaveUser(BotUser botUser, Optional<UserDto> registeredUserByEmail, Optional<UserDto> registeredUserByTgId) {
        String output;
        if (registeredUserByEmail.isEmpty() && registeredUserByTgId.isEmpty()) {
            try {
                dataStorageClient.saveUser(userDtoMapper.toUserDto(botUser));
            } catch (Exception e) {
                log.error(e.getMessage());
                return "Произошла ошибка, пожалуйста, попробуйте выполнить действие позднее";
            }
            log.debug("Пользователь с email {} и telegramId {} сохранен", botUser.getEmail(), botUser.getTelegramUserId());
            output = "Спасибо за регистрацию! Мы уведомим вас, как только начнется прием заявок по направлениям";
        } else if (registeredUserByEmail.isPresent()) {
            UserDto userDto = registeredUserByEmail.get();
            updateFieldsIfApplicable(botUser, userDto);
            output = "Вы уже регистрировались в системе, мы уведомим вас, как только начнется прием заявок по направлениям";
        } else {
            UserDto userDto = registeredUserByTgId.get();
            updateFieldsIfApplicable(botUser, userDto);
            output = "Вы уже регистрировались в системе, мы уведомим вас, как только начнется прием заявок по направлениям";
        }
        return output;
    }

    private void updateFieldsIfApplicable(BotUser botUser, UserDto userDto) {
        userDto.setFirstName(botUser.getFirstName());
        userDto.setLastName(botUser.getLastName());
        userDto.setCity(botUser.getCity());
        userDto.setPhone(botUser.getPhone());
    }

    private String processStartCommand(Update update) {
        log.debug("вызов метода processStartCommand, telegramId = {}", update.getMessage().getFrom().getId());
        Long telegramId = update.getMessage().getFrom().getId();
        BotUser user = BotUser.builder()
                .telegramUserId(telegramId)
                .role(UserRole.VISITOR)
                .state(UserState.START)
                .build();
        users.put(telegramId, user);
        log.debug("Пользователь с телеграм id = {} сохранен", telegramId);
        //todo вернуть приветствие, извещение о персональных данных и 1 кнопку (заявка либо предзаявка)
        return "Здравствуйте! \n" +
                "/оставить предзаявку\n" +
                "/подать заявку\n";
    }

    private String help() {
        return "Список доступных команд:\n"
                //+ "/cancel - отмена выполнения текущей команды;\n"
                + "/оставить предзаявку\n"
                + "/подать заявку\n";
        //todo команда какая-то одна в зависимости от логики с датой
    }

    private String processHelpCommand(Update update) {
        return help();
    }

    private String processSubmitPrerequest(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();
        BotUser botUser = users.get(telegramId);
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

    private void setCourses() throws Exception {
        List<String> currentCourses = dataStorageClient.getCourses();
        for (String course : currentCourses) {
            courses.put(course, new Object());
        }
    } //todo вызов, когда запускается обучение только
}
