package io.eagle44.githubinsights.languages;

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
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LanguagesServiceIntegrationTest {
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
                // return repos for user with two repos
                if (request.getPath().contains("/users/microsoft/repos?per_page=2")) {
                    try {
                        return new MockResponse()
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(Files.readString(Paths.get("src/main/resources/mock-responses/2-repos.json")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // return repos for user with no repos
                if (request.getPath().contains("/users/userWithoutRepos/")) {
                    return new MockResponse()
                            .addHeader("Content-Type", "application/json; charset=utf-8")
                            .setBody("[]");
                }
                // return repos for user with 200 repos (two times the same list)
                if (request.getPath().contains("/users/microsoft/repos?per_page=100")) {
                    try {
                        return new MockResponse()
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(Files.readString(Paths.get("src/main/resources/mock-responses/100-repos.json")));
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
    @DisplayName("Languages are returned correctly for user with 2 repos")
    public void getLanguages() throws Exception {
        // Expected result:
        LinkedHashMap<String, Long> expectedResult = new LinkedHashMap<>();
        expectedResult.put("TypeScript", 4L);
        expectedResult.put("Jupyter Notebook", 2L);

        // Actual result:
        LanguagesClient languagesClient = new LanguagesClient(baseUrl);
        LanguagesService languagesService = new LanguagesService(languagesClient);
        Languages languages = languagesService.getLanguages("microsoft", 2);
        HashMap<String, Long> actualResult = languages.getLanguagesSortedDesc();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Empty languages HashMap is returned for user with no repos")
    public void getLanguagesForUserWithNoRepos() throws Exception {
        // Expected result:
        LinkedHashMap<String, Long> expectedResult = new LinkedHashMap<>();

        // Actual result:
        LanguagesClient languagesClient = new LanguagesClient(String.format("http://localhost:%s", server.getPort()));
        LanguagesService languagesService = new LanguagesService(languagesClient);
        Languages languages = languagesService.getLanguages("userWithoutRepos", 30);
        HashMap<String, Long> actualResult = languages.getLanguagesSortedDesc();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Languages are returned correctly for user with 200 repos")
    public void getLanguagesForUserWith200Repos() throws Exception {
        // Expected result:
        LinkedHashMap<String, Long> expectedResult = new LinkedHashMap<>();
        expectedResult.put("TypeScript", 400L);
        expectedResult.put("Jupyter Notebook", 200L);

        // Actual result:
        LanguagesClient languagesClient = new LanguagesClient(baseUrl);
        LanguagesService languagesService = new LanguagesService(languagesClient);
        Languages languages = languagesService.getLanguages("microsoft",200);
        HashMap<String, Long> actualResult = languages.getLanguagesSortedDesc();

        assertEquals(expectedResult, actualResult);
    }
}