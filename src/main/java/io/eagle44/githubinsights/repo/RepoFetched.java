package io.eagle44.githubinsights.repo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RepoFetched {
    String name;
    String languagesUrl;

    public RepoFetched() {
    }

    public RepoFetched(String name, String languagesUrl) {
        this.name = name;
        this.languagesUrl = languagesUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguagesUrl() {
        return languagesUrl;
    }

    public void setLanguagesUrl(String languagesUrl) {
        this.languagesUrl = languagesUrl;
    }
}
