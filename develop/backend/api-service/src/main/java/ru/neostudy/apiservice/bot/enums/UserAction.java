package ru.neostudy.apiservice.bot.enums;

import java.util.Optional;

public enum UserAction {
    SUBMIT_REQUEST("Подать заявку"),
    SUBMIT_PREREQUEST("Оставить предзаявку");

    private final String action;

    UserAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }

    public static Optional<UserAction> fromValue(String v) {
        for (UserAction c : UserAction.values()) {
            if (c.action.equals(v)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}
