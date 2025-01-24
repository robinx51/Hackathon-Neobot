package ru.neostudy.datastorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "ru.neostudy.datastorage.db.entity")
@EnableJpaRepositories("ru.neostudy.datastorage.db.repository")
public class DataStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataStorageApplication.class, args);
    }
}
