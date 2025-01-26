package ru.neostudy.apiservice.bot.enums;

public enum UserState {
    START,
    WAIT_FOR_FIRSTNAME,
    WAIT_FOR_LASTNAME,
    WAIT_FOR_CITY,
    WAIT_FOR_EMAIL,
    WAIT_FOR_PHONE,
    WAIT_FOR_COURSE_REQUEST,
    WAIT_FOR_COURSE_NOTIFICATION,
    COMPLETE
}
