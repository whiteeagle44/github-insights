package io.eagle44.githubinsights.user;

import io.eagle44.githubinsights.languages.Languages;
import io.eagle44.githubinsights.languages.LanguagesClient;
import io.eagle44.githubinsights.languages.LanguagesService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceIntegrationTest {
    private static MockWebServer server;
    private static String baseUrl;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = String.format("http://localhost:%s", server.getPort());

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                // return user info
                if (request.getPath().equals("/users/microsoft")) {
                    try {
                        return new MockResponse()
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(Files.readString(Paths.get("src/main/resources/mock-responses/user.json")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // return repos for user with two repos
                if (request.getPath().equals("/users/microsoft/repos?per_page=2&page=1")) {
                    try {
                        return new MockResponse()
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(Files.readString(Paths.get("src/main/resources/mock-responses/2-repos.json")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // return languages of repo1 for all languages queries
                if (request.getPath().contains("/repos/microsoft/")) {
                    try {
                        return new MockResponse()
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(Files.readString(Paths.get("src/main/resources/mock-responses/languages-repo1.json")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        server.setDispatcher(dispatcher);
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    @DisplayName("User is returned correctly for user with two repos")
    void getUser() {
        // Expected result:
        Languages languages = new Languages();
        LinkedHashMap<String, Long> languagesMap = new LinkedHashMap<>();
        languagesMap.put("TypeScript", 4L);
        languagesMap.put("Jupyter Notebook", 2L);
        languages.addLanguages(languagesMap);
        User expectedResult = new User("microsoft", "Microsoft", "Open source projects and samples from Microsoft", languages);

        // Actual result:
        UserClient userClient = new UserClient(baseUrl);
        LanguagesService languagesService = new LanguagesService(new LanguagesClient(baseUrl));
        UserService userService = new UserService(userClient, languagesService);
        User actualResult = userService.getUser("microsoft", 2);

        assertEquals(expectedResult, actualResult);
    }
}
