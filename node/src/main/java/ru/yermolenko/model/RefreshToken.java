package ru.yermolenko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import ru.yermolenko.tools.Constants;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@RedisHash(value = "refresh_tokens")
public class RefreshToken implements Serializable {
    @Id
    @Unique
    private String token;
    @Indexed
    @Unique
    private String username;
    private Instant expiryDate;
    @TimeToLive
    private long timeToLive = Constants.getTimeToLive();
}
