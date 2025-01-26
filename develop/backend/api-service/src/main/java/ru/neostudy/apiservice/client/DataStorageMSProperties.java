package ru.neostudy.apiservice.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "storage")
@Getter
@Component
@Setter
public class DataStorageMSProperties {
    private String serverUrl;
    private String saveUserUri;  //todo
    private String getUserByEmailUri; //todo
    private String getUserByTelegramUri; //todo
    //private String updateUserUri;  //todo
    private String getCoursesUri;
    public String getUsersWithoutCourseUri;
}
