package io.eagle44.githubinsights.repo;

import io.eagle44.githubinsights.languages.LanguagesClient;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class RepoServiceIntegrationTest {
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
                if (request.getPath().equals("/users/microsoft/repos?per_page=2&page=1")) {
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
    @DisplayName("Repository is returned correctly for user with 2 repos")
    void getRepository() {
        // Expected result:
        Repo repo1 = new Repo(".Net-Interactive-Kernels-ADS", Map.of("Jupyter Notebook", 1L,
                "TypeScript", 2L));
        Repo repo2 = new Repo(".NET-Modernization-In-a-Day", Map.of("Jupyter Notebook", 1L,
                "TypeScript", 2L));
        List<Repo> repos = List.of(repo1, repo2);
        Repository expectedResult = new Repository(repos, null);

        // Actual result:
        RepoClient repoClient = new RepoClient(baseUrl);
        LanguagesClient languagesClient = new LanguagesClient(baseUrl);
        RepoService repoService = new RepoService(repoClient, languagesClient);
        Repository actualResult = repoService.getRepository("microsoft", 2, 1);

        assertIterableEquals(expectedResult.getRepos(), actualResult.getRepos());
        assertEquals(expectedResult.getPagination(), actualResult.getPagination());
    }

    @Test
    @DisplayName("Repository is returned correctly for user with no repos")
    void getRepositoryForUserWithNoRepos() {
        // Expected result:
        Repository expectedResult = new Repository(new ArrayList<>(), null);

        // Actual result:
        RepoClient repoClient = new RepoClient(baseUrl);
        LanguagesClient languagesClient = new LanguagesClient(baseUrl);
        RepoService repoService = new RepoService(repoClient, languagesClient);
        Repository actualResult = repoService.getRepository("userWithoutRepos", 30, 1);

        assertIterableEquals(expectedResult.getRepos(), actualResult.getRepos());
        assertEquals(expectedResult.getPagination(), actualResult.getPagination());
    }
}