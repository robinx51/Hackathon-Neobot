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
    private String saveUserUri;
    private String getUserByEmailUri;
    private String getUserByTelegramUri;
    private String getCoursesUri;
    public String getUsersWithoutCourseUri;
    private String updateStatementUri;
    private String getStatementFullInfoUri;
    private String getCompleteStatements;

}