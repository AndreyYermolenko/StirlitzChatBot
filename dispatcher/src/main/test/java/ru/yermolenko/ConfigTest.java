package ru.yermolenko;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class ConfigTest {
    @Value("${server.port}")
    private int serverPort;
    @Value("${botpath}")
    private String botpath;
    @Value("${botname}")
    private String botname;
    @Value("${token}")
    private String token;
    @Value("${server.ssl.enabled}")
    private boolean sslEnabled;
    @Value("${server.ssl.key-store}")
    private String keyStore;
    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${server.ssl.key-store-type}")
    private String keyStoreType;
    @Value("${server.ssl.key-alias}")
    private String keyStoreAlias;

    @Test
    void testServerPort() {
        Set<Integer> allowedPorts = Set.of(443, 80, 88, 8443);
        if (!allowedPorts.contains(serverPort)) {
            fail("Server port isn't allowed! Please use 443, 80, 88 or 8443.");
        }
    }

    @Test
    void testBotPath() {
        assertThat(botpath, Matchers.containsString("https://"));
    }

    @Test
    void testBotName() {
        if ("".equals(botname)) {
            fail("botname can't be empty!");
        }
    }

    @Test
    void testToken() {
        if ("".equals(token)) {
            fail("token can't be empty!");
        }
    }

    @Test
    void testSslEnabled() {
        if (!sslEnabled) {
            fail("ssl should be enabled!");
        }
    }

    @Test
    void testKeyStore() {
        if ("".equals(keyStore)) {
            fail("ssl.key-store can't be empty!");
        }
    }

    @Test
    void testKeyStorePassword() {
        if ("".equals(keyStorePassword)) {
            fail("ssl.key-store-password can't be empty!");
        }
    }

    @Test
    void testKeyStoreType() {
        if ("".equals(keyStoreType)) {
            fail("ssl.key-store-type can't be empty!");
        }
    }

    @Test
    void testKeyStoreAlias() {
        if ("".equals(keyStoreAlias)) {
            fail("server.ssl.key-alias can't be empty!");
        }
    }
}
