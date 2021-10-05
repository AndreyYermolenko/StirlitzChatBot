package ru.yermolenko.tools;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class NumberValidator {

    public Result parsNumber(String data) {
        Result res = new Result();
        try {
            BigInteger number = new BigInteger(data);
            res.setNumber(number);
        } catch (NumberFormatException | NullPointerException e) {
            res.setError(true);
        }
        return res;
    }

    @Data
    public static class Result {
        private boolean error;
        private BigInteger number;

        public boolean hasError() {
            return error;
        }
    }
}
