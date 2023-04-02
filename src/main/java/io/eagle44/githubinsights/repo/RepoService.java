package io.eagle44.githubinsights.repo;

import io.eagle44.githubinsights.languages.LanguagesClient;
import io.eagle44.githubinsights.util.LinkHeaderParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RepoService {
    private final RepoClient repoClient;
    private final LanguagesClient languagesClient;

    public RepoService(RepoClient repoClient, LanguagesClient languagesClient) {
        this.repoClient = repoClient;
        this.languagesClient = languagesClient;
    }

    public Repository getRepository(String username, int perPage, int page) {
        ResponseEntity<List<RepoFetched>> response = repoClient.queryGithubApiForRepos(username, perPage, page).block();
        List<String> linkHeader = response.getHeaders().get(HttpHeaders.LINK);
        Pagination pagination = null;
        if(!Objects.isNull(linkHeader)) {
            pagination = LinkHeaderParser.parse(linkHeader);
        }

        List<RepoFetched> reposFetched = response.getBody();
        List<Repo> repos = new ArrayList<>(reposFetched.size());

        Flux.range(0, reposFetched.size())
                .flatMap(i -> languagesClient.queryGithubApiForLanguages(reposFetched.get(i).getLanguagesUrl()))
                .index()
                .doOnNext(languagesOfOneRepoResponse -> {
                    int index = Math.toIntExact(languagesOfOneRepoResponse.getT1());
                    Map<String, Long> languagesOfOneRepo = languagesOfOneRepoResponse.getT2().getBody();
                    if (Objects.nonNull((languagesOfOneRepo))) {
                        repos.add(new Repo(reposFetched.get(index).getName(), languagesOfOneRepo));
                    }
                })
                .blockLast();

        return new Repository(repos, pagination);
    }
}
