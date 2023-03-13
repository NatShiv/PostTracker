package com.example.posttracker.constant;

public enum Status {
    WAITING ("Принято, Ожидает отправки."),
    SHIPPED("Отправлено в ПО "),
    RECEIVED("Получено");
    private final String message;

    Status(String message) {
        this.message = message;
        }

    public String getMessage() {
        return message;
    }
}
