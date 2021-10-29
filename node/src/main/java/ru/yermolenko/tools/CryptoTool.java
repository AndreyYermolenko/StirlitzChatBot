package ru.yermolenko.tools;

import lombok.extern.log4j.Log4j;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class CryptoTool {
    private static final String digits = "Pq12GSnZ5CwkrmutojxQbMl3cRi8speFya760ONUYKgThHAWJIfLdD9V4XEvzB";
    private final static Hashids hashids = new Hashids(digits);
    @Value("${salt}")
    private Long GAMMA_OFFSET;

    public String hashOf(Long value) {
        return hashids.encode(GAMMA_OFFSET + value);
    }

    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0] - GAMMA_OFFSET;
        }
        return null;
    }
}
