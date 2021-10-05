package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.yermolenko.service.CollatzService;
import ru.yermolenko.tools.NumberValidator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j
public class CollatzServiceImpl implements CollatzService {
    private final NumberValidator numberValidator;

    public CollatzServiceImpl(NumberValidator numberValidator) {
        this.numberValidator = numberValidator;
    }

    @Override
    public String processInput(String input) {
        NumberValidator.Result res = numberValidator.parsNumber(input);
        if (!res.hasError()) {
            Map<String, BigInteger> results = calculate(res.getNumber());
            return results.toString();
        } else {
            return "Пожалуйста, введите число!";
        }
    }

    private Map<String, BigInteger> calculate(BigInteger number) {
        Map<String, BigInteger> result = new HashMap<>();
        long i = 0;
        BigInteger maxValue = BigInteger.ZERO;
        while (number.compareTo(BigInteger.ONE) > 0) {
            BigInteger modNumber = number.mod(BigInteger.TWO);
            if (modNumber.compareTo(BigInteger.ZERO) == 0) {
                number = number.divide(BigInteger.TWO);
            } else {
                number = number.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE);
            }

            if (number.compareTo(maxValue) > 0) {
                maxValue = number;
            }

            i++;
        }
        result.put("countOfSteps", BigInteger.valueOf(i));
        result.put("maxValue", maxValue);
        return result;
    }
}
