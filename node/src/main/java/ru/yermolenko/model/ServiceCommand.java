package ru.yermolenko.model;

public enum ServiceCommand {
    HELP("/help"),
    GET_CHAT_ID("/get_chat_id"),
    REGISTRATION("/registration"),
    COLLATZ("/collatz"),
    ABORT("/abort"),
    START("/start");
    private final String cmd;

    ServiceCommand(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String string) {
        return this.toString().equals(string);
    }
}
