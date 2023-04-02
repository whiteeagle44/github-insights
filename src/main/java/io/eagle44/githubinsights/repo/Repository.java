package io.eagle44.githubinsights.repo;

import java.util.List;

public class Repository {
    private List<Repo> repos;
    private Pagination pagination;

    public Repository() {
    }

    public Repository(List<Repo> repos, Pagination pagination) {
        this.repos = repos;
        this.pagination = pagination;
    }

    public List<Repo> getRepos() {
        return repos;
    }

    public void setRepos(List<Repo> repos) {
        this.repos = repos;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
