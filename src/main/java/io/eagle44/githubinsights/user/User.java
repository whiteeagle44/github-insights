package io.eagle44.githubinsights.user;

import io.eagle44.githubinsights.languages.Languages;

import java.util.Objects;

public class User {
    String login;
    String name;
    String bio;
    Languages languages;

    public User() {
    }

    public User(String login, String name, String bio, Languages languages) {
        this.login = login;
        this.name = name;
        this.bio = bio;
        this.languages = languages;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Languages getLanguages() {
        return languages;
    }

    public void setLanguages(Languages languages) {
        this.languages = languages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login) && Objects.equals(name, user.name) && Objects.equals(bio, user.bio) && Objects.equals(languages, user.languages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, name, bio, languages);
    }
}
