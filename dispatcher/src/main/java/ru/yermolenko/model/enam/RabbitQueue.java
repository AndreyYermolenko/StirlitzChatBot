package ru.yermolenko.model.enam;

public enum RabbitQueue {
    DOC_MESSAGE_RECORD("doc_message_record"),
    PHOTO_MESSAGE_RECORD("photo_message_record"),
    TEXT_MESSAGE_RECORD("text_message_record"),
    GENERAL_RECORD("general_record");
    private final String topic;

    RabbitQueue(String queue) {
        this.topic = queue;
    }

    @Override
    public String toString() {
        return topic;
    }
}
