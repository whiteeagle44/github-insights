package io.eagle44.githubinsights.user;

import io.eagle44.githubinsights.languages.Languages;
import io.eagle44.githubinsights.languages.LanguagesService;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserClient userClient;
    private final LanguagesService languageService;

    public UserService(UserClient userClient, LanguagesService languageService) {
        this.userClient = userClient;
        this.languageService = languageService;
    }

    public User getUser(String username, int repos) {
        UserFetched userFetched = userClient.queryGithubApiForUser(username).getBody();
        Languages languages = languageService.getLanguages(username, repos);
        return new User(userFetched.getLogin(), userFetched.getName(), userFetched.getBio(), languages);
    }
}
