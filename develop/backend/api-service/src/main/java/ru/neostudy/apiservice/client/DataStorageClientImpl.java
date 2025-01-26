package ru.neostudy.apiservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import ru.neostudy.apiservice.bot.Course;
import ru.neostudy.apiservice.client.interfaces.DataStorageClient;
import ru.neostudy.apiservice.model.StatementFullDto;
import ru.neostudy.apiservice.model.UpdateStatementDto;
import ru.neostudy.apiservice.model.User;
import ru.neostudy.apiservice.model.UserDto;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataStorageClientImpl implements DataStorageClient {
    private final WebClient webClient;
    private final DataStorageMSProperties dataStorageMSProperties;

    @Override
    public Optional<User> getUserByEmail(String email) throws Exception {
        log.debug("Вызов метода getUserByEmail с email {}", email);
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getGetUserByEmailUri()), email)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<Optional<User>>() {
                })
                .block();
    }


    @Override
    public Optional<User> getUserByTelegramId(Long telegramId) throws Exception {
        log.debug("Вызов метода getUserByTelegramId с telegramId {}", telegramId);
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getGetUserByTelegramUri()), telegramId)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<Optional<User>>() {
                })
                .block();
    }

    @Override
    public UserDto saveUser(UserDto userDto) throws Exception {
        log.debug("Вызов метода saveUser с email {} и telegramId {}", userDto.getEmail(), userDto.getTelegramUserId());
        return webClient.post()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getSaveUserUri()))
                .bodyValue(userDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(UserDto.class)
                .block();
    }

    @Override
    public List<Course> getCourses() throws Exception {
        log.debug("Вызов метода getCourses");
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getGetCoursesUri()))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<List<Course>>() {
                })
                .block();
    }

    @Override
    public List<User> getUsersWithoutCourse() throws Exception {
        log.debug("Вызов метода getUsersWithoutCourse");
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getGetUsersWithoutCourseUri()))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<List<User>>() {
                })
                .block();
    }

    @Override
    public void updateStatementStatus(UpdateStatementDto statementDto) {
        log.debug("Вызов метода updateStatementStatus с statementDto {}", statementDto);
        webClient.put()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getUpdateStatementUri()))
                .bodyValue(statementDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public StatementFullDto getCompleteStatementById(Integer id) throws Exception {
        log.debug("Вызов метода getCompleteStatementById");
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getGetStatementFullInfoUri()), id)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(StatementFullDto.class)
                .block();
    }

    @Override
    public List<StatementFullDto> getCompleteStatements() {
        log.debug("Вызов метода getCompleteStatements");
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getGetCompleteStatements()))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<List<StatementFullDto>>() {
                })
                .block();
    }
}

