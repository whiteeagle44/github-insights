package io.eagle44.githubinsights.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "{username}", produces = "application/json")
    public User getUser(@PathVariable String username, @RequestParam(defaultValue = "100") String repos) {
        return userService.getUser(username, Integer.parseInt(repos));
    }
}