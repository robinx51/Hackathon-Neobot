package ru.neostudy.apiservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import ru.neostudy.apiservice.client.interfaces.DataStorageClient;
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
    public Optional<UserDto> getUserByEmail(String email) throws Exception {
        log.debug("Вызов метода getUserByEmail с email {}", email);
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl(), uriBuilder ->
                        uriBuilder
                                .path(dataStorageMSProperties.getGetUserByEmailUri())
                                .queryParam("email", email)
                                .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<Optional<UserDto>>() {
                })
                .block();
    }


    @Override
    public Optional<UserDto> getUserByTelegramId(Long telegramId) throws Exception {
        log.debug("Вызов метода getUserByTelegramId с telegramId {}", telegramId);
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl(), uriBuilder ->
                        uriBuilder
                                .path(dataStorageMSProperties.getGetUserByEmailUri())
                                .queryParam("telegramId", telegramId)
                                .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<Optional<UserDto>>() {
                })
                .block();
    }

    @Override
    public UserDto saveUser(UserDto userDto) throws Exception {
        log.debug("Вызов метода saveUser с email {} и telegramId {}", userDto.getEmail(), userDto.getTelegramId());
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
    public List<String> getCourses() throws Exception {
        log.debug("Вызов метода getCourses");
        return webClient.get()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getGetCoursesUri()))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();
    }
/*
    @Override
    public UserDto updateUser(UserDto userDto) throws Exception {
        log.debug("Вызов метода updateUser с email {} и telegramId {}", userDto.getEmail(), userDto.getTelegramUserId());
        return webClient.post()
                .uri(dataStorageMSProperties.getServerUrl().concat(dataStorageMSProperties.getSaveUserUri()))
                .bodyValue(userDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        ClientResponse::createException)
                .bodyToMono(UserDto.class)
                .block();
    }*/
}

