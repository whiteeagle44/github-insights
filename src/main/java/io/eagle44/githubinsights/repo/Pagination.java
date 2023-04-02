package io.eagle44.githubinsights.repo;

public class Pagination {
    private String prevPage;
    private String nextPage;
    private String firstPage;
    private String lastPage;

    public Pagination() {
    }

    public Pagination(String prevPage, String nextPage, String firstPage, String lastPage) {
        this.prevPage = prevPage;
        this.nextPage = nextPage;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
    }

    public String getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(String prevPage) {
        this.prevPage = prevPage;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public String getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

    public String getLastPage() {
        return lastPage;
    }

    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }
}
