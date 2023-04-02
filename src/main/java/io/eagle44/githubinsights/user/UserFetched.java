package io.eagle44.githubinsights.user;

public class UserFetched {
    String login;
    String name;
    String bio;

    public UserFetched() {
    }

    public UserFetched(String login, String name, String bio) {
        this.login = login;
        this.name = name;
        this.bio = bio;
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
}
