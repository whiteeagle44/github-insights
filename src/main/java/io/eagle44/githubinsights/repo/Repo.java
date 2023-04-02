package io.eagle44.githubinsights.repo;

import java.util.Map;
import java.util.Objects;

public class Repo {
    String name;
    Map<String, Long> languages;

    public Repo() {
    }

    public Repo(String name, Map<String, Long> languages) {
        this.name = name;
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Long> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<String, Long> languages) {
        this.languages = languages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repo repo = (Repo) o;
        return Objects.equals(name, repo.name) && Objects.equals(languages, repo.languages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, languages);
    }
}
