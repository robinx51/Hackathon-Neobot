package ru.neostudy.datastorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDataDto {
    @Pattern(regexp = "^[a-zA-Z0-9_]{5,32}$", message = "Можно использовать буквы латинского алфавита, цифры и \"_\", длина от 5 до 32 символов")
    @Schema(name = "telegramId", example = "@telegramId")
    private String telegramId;

    @NotNull
    @Pattern(regexp = "^[а-яёА-ЯЁa-zA-Z]{2,32}$", message = "Имя - от 2 до 32 букв")
    @Schema(name = "firstName", example = "Андрей", pattern = "^[а-яёА-ЯЁa-zA-Z]{2,32}$")
    private String firstName;

    @NotNull
    @Pattern(regexp = "^[а-яёА-ЯЁa-zA-Z]{2,32}$", message = "Фамилия - от 2 до 32 букв")
    @Schema(name = "lastName", example = "Сидоров", pattern = "^[а-яёА-ЯЁa-zA-Z]{2,32}$")
    private String lastName;

    @NotNull
    @Pattern(regexp = "^[а-яёА-ЯЁa-zA-Z]{2,25}$", message = "Город - от 2 до 25 букв")
    @Schema(name = "city", example = "Пенза", pattern = "^[а-яёА-ЯЁa-zA-Z]{2,25}$")
    private String city;

    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "Номер телефона - 10 цифр")
    @Schema(name = "phoneNumber", example = "9876543210", pattern = "^\\d{10}")
    private String phoneNumber;

    @NotNull @Email
    @Schema(name = "email", example = "example@mail.ru", pattern = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$")
    private String email;
}