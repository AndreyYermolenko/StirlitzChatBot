package ru.yermolenko.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    private static Long timeToLive;

    public Constants(@Value("${redis.time-to-live}") Long time) {
        if (time != null) {
            timeToLive = time;
        }
    }

    public static Long getTimeToLive() {
        return timeToLive;
    }
}
