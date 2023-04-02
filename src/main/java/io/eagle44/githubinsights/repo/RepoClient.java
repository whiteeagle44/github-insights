package io.eagle44.githubinsights.repo;

import io.eagle44.githubinsights.exceptions.NotFoundException;
import io.eagle44.githubinsights.exceptions.ServiceUnavailableException;
import io.eagle44.githubinsights.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RepoClient {
    private final WebClient.Builder builder;

    @Value("${base-url}")
    private String baseUrl;

    @Value("${github-token}")
    private String githubToken;

    @Autowired
    public RepoClient(WebClient.Builder builder) {
        this.builder = builder;
    }

    public RepoClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.builder = WebClient.builder();
    }

    public Mono<ResponseEntity<List<RepoFetched>>> queryGithubApiForRepos(String username, int perPage, int page) throws ResponseStatusException {
        Predicate<Throwable> is5xx =  (throwable) -> throwable instanceof WebClientResponseException && ((WebClientResponseException)throwable).getStatusCode().is5xxServerError();
        Retry retry5xx = Retry.backoff(2, Duration.ofSeconds(2))
                .filter(is5xx)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw new ServiceUnavailableException("External service failed to process after max retries");
                });

        return builder
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.newConnection().compress(true)))
                .build()
                .get()
                .uri(buildRepoQueryUri(username, perPage, page))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(new NotFoundException("Not found")))
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> Mono.error(new UnauthorizedException("Bad credentials")))
                .toEntityList(RepoFetched.class)
                .retryWhen(retry5xx);
    }

    private URI buildRepoQueryUri(String username, int perPage, int page) {
        String repoQueryUri = baseUrl + "/users/" + username + "/repos" + "?per_page=" + perPage + "&page=" + page;
        return URI.create(repoQueryUri);
    }

}
