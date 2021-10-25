package ru.yermolenko.model;

public enum LinkType {
    GET_DOC("api/file/get_doc"),
    GET_PHOTO("api/file/get_photo");
    private final String link;

    LinkType(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return link;
    }
}
