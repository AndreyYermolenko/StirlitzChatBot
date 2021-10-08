package ru.yermolenko;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class ConfigTest {
    private String serverPort;
    private String linkAddress;
    private String datasourceUrl;
    private String datasourceUsername;
    private String datasourcePassword;
    private String jpaGenerateDdl;
    private String token;
    private String fileInfoUrl;
    private String fileStorageUrl;
    private String salt;

    {
        try (InputStream in = ConfigTest.class
                .getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            serverPort = properties.getProperty("server.port");
            linkAddress = properties.getProperty("link.address");
            datasourceUrl = properties.getProperty("spring.datasource.url");
            datasourceUsername = properties.getProperty("spring.datasource.username");
            datasourcePassword = properties.getProperty("spring.datasource.password");
            jpaGenerateDdl = properties.getProperty("spring.jpa.generate-ddl");
            token = properties.getProperty("token");
            fileInfoUrl = properties.getProperty("service.file_info.url");
            fileStorageUrl = properties.getProperty("service.file_storage.url");
            salt = properties.getProperty("salt");
        } catch (IOException e) {
            fail("application.properties isn't found or can't be read!");
            e.printStackTrace();
        }
    }

    @Test
    void testServerPort() {
        Integer.parseInt(serverPort);
    }

    @Test
    void testLinkAddress() {
        if ("".equals(linkAddress)) {
            fail("link.address can't be empty!");
        }
    }

    @Test
    void testDatasourceUrl() {
        assertThat(datasourceUrl, Matchers.containsString("jdbc:postgresql://"));
    }

    @Test
    void testDatasourceUsername() {
        if ("".equals(datasourceUsername)) {
            fail("datasource.username can't be empty!");
        }
    }

    @Test
    void testDatasourcePassword() {
        if ("".equals(datasourcePassword)) {
            fail("datasource.password can't be empty!");
        }
    }

    @Test
    void testJpaGenerateDdl() {
        boolean ddl = Boolean.parseBoolean(jpaGenerateDdl);
        if (!ddl) {
            fail("jpa.generate.ddl should be true!");
        }
    }

    @Test
    void testToken() {
        if ("".equals(token)) {
            fail("token can't be empty!");
        }
    }

    @Test
    void testFileInfoUrl() {
        assertThat(fileInfoUrl, Matchers.containsString("https://api.telegram.org/bot"));

    }

    @Test
    void testFileStorageUrl() {
        assertThat(fileStorageUrl, Matchers.containsString("https://api.telegram.org/file/bot"));

    }

    @Test
    void testSalt() {
        Long.parseLong(salt);
    }
}
