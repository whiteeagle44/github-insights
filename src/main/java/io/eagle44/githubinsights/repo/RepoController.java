package io.eagle44.githubinsights.repo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users/")
public class RepoController {
    private final RepoService repoService;

    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping(value = "{username}/repos", produces = "application/json")
    public Repository getRepos(@PathVariable String username, @RequestParam(defaultValue = "30") String per_page, @RequestParam(defaultValue = "1") String page) {
        return repoService.getRepository(username, Integer.parseInt(per_page), Integer.parseInt(page));
    }
}
