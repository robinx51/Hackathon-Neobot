package ru.neostudy.apiservice.bot.enums;

import java.util.Optional;

public enum ServiceCommand {
    START("/start"),
    HELP("/help"),
    SUBMIT_REQUEST("/submit"),
    SUBMIT_PREREQUEST("/submit_now"),
    CHOOSE_COURSE("/make_life_choice");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Optional<ServiceCommand> fromValue(String v) {
        for (ServiceCommand command : ServiceCommand.values()) {
            if (v.equals(command.value)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }
}