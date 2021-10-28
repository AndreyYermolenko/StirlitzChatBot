package ru.yermolenko.model;

public enum ServiceCommand {
    HELP("/help"),
    GET_API_KEY("/get_api_key"),
    GET_CHAT_ID("/get_chat_id"),
    REGISTRATION("/registration"),
    ABORT("/abort");
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
