package ru.neostudy.apiservice.model.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class AppUserValidator {
    Pattern namePattern = Pattern.compile("^[а-яёА-ЯЁ]{2,30}$");
    Pattern emailPattern = Pattern.compile("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$");
    Pattern phonePattern = Pattern.compile("^\\+7\\d{10}$");
    Pattern cityPattern = Pattern.compile("^[а-яёА-ЯЁ]{2,35}$");

    public boolean isNameValid(String name) {
        return namePattern.matcher(name).find();
    }

    public boolean isEmailValid(String email) {
        return emailPattern.matcher(email).find();
    }

    public boolean isCityValid(String city) {
        return cityPattern.matcher(city).find();
    }

    public String formatPhone(String phone) {
        String formattedPhone = phone;

        if (formattedPhone.contains("-")) {
            formattedPhone = formattedPhone.replaceAll("-", "");
        }
        if (formattedPhone.contains(" ")) {
            formattedPhone = formattedPhone.replaceAll(" ", "");
        }
        if (formattedPhone.length() == 11) {
            formattedPhone = "+7".concat(formattedPhone.substring(1, 11));
        } else if (formattedPhone.length() == 10) {
            formattedPhone = "+7".concat(formattedPhone);
        }
        log.debug("formattedPhone: {}", formattedPhone); //todo
        return formattedPhone;
    }

    public boolean isPhoneValid(String phone) {
        return phonePattern.matcher(phone).find();
    }
}
