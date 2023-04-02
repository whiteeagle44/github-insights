package io.eagle44.githubinsights.user;

import io.eagle44.githubinsights.exceptions.ServiceUnavailableException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserClientIntegrationTest {
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
                // return server error
                if (request.getPath().equals("/users/serverError")) {
                    return new MockResponse().setResponseCode(500);
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
    @DisplayName("503 Service Unavailable is returned for 5xx error after failed retries")
    void queryGithubApiForUserGet5xxErrorRepeatedly() {
        // Actual result:
        UserClient userClient = new UserClient(baseUrl);
        LanguagesService languagesService = new LanguagesService(new LanguagesClient(baseUrl));
        UserService userService = new UserService(userClient, languagesService);

        ServiceUnavailableException thrown = assertThrows(
                ServiceUnavailableException.class,
                () -> userService.getUser("serverError", 2),
                "Expected getUser() to throw, but it didn't"
        );
    }
}
