package io.eagle44.githubinsights.languages;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LanguagesService {
    private final LanguagesClient languagesClient;

    public LanguagesService(LanguagesClient languagesClient) {
        this.languagesClient = languagesClient;
    }

    public Languages getLanguages(String username, int repos) {
        List<String> languagesUrls = getLanguagesUrls(username, repos);
        Languages languages = new Languages();

        Flux.range(0, languagesUrls.size())
                        .flatMap(i -> languagesClient.queryGithubApiForLanguages(languagesUrls.get(i)))
                                .doOnNext(languagesOfOneRepo -> {
                                    if (Objects.nonNull(languagesOfOneRepo.getBody())) {
                                        languages.addLanguages(languagesOfOneRepo.getBody());
                                    }
                                })
                                        .blockLast();
        return languages;
    }

    public List<String> getLanguagesUrls(String username, int repos) {
        int pages = (int) Math.ceil((double) repos / 100); // e.g. repos=120 -> pages=2; repos = 50 -> pages=1

        List<String> languagesUrls = new ArrayList<>(repos);
        List<LanguagesUrl> partialLanguagesUrls;

        for (int currentPage = 1; currentPage <= pages; currentPage++) {
            if (currentPage == pages ) {
                // e.g. repos=260, query for 60 repos from 3rd page (not 100)
                partialLanguagesUrls = languagesClient
                        .queryGithubApiForLanguagesUrls(username, repos - 100 * (pages - 1), currentPage)
                        .getBody();
            } else {
                partialLanguagesUrls = languagesClient
                        .queryGithubApiForLanguagesUrls(username, 100, currentPage)
                        .getBody();
            }
            if (!Objects.isNull(partialLanguagesUrls)) {
                languagesUrls.addAll(partialLanguagesUrls
                        .stream()
                        .map(LanguagesUrl::getLanguagesUrl)
                        .collect(Collectors.toList()));
            }
        }
        return languagesUrls;
    }
}
