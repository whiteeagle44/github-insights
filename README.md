# GitHub Insights

Welcome to the documentation of GitHub Insights, an application that provides you with useful information about Github users and their repositories.

GitHub Insights is an open-source project that uses the GitHub API to fetch information about a specified user. With GitHub Insights, you can fetch information about the user's repositories and languages used, and you can also obtain the user's personal information, such as their name, login, bio, and most popular languages.

This documentation provides an overview of the project's features, setup instructions, API endpoint details, and technical details. In addition, it highlights some essential notes on what's new and future developments.

GitHub Insights is perfect for anyone who wants to gain insights into a GitHub user's repositories and languages. Now, let's dive into the details of GitHub Insights and learn how to use it to your advantage.

## Features

* Firstly, it can list all the repositories created by a given Github user, displaying the repository name alongside the programming languages used. This feature proves particularly useful in streamlining the software development process, allowing developers to quickly identify and track the progress of their repositories.

* Secondly, the application is equipped with a feature that allows it to list user information and their most popular languages. This feature provides comprehensive insights into a user's Github activity, displaying details such as their login, name, and bio alongside the number of bytes of code written in their most frequently used programming languages. 

Overall, these features serve as invaluable tools for developers looking to streamline their workflows and expand their knowledge of Github usage.

## Setup

1. Set the `GITHUB_TOKEN` environment variable. You can generate one in Github developer settings of your account.
2. Build and run the application from the IDE. Alternatively in command line with `./gradlew build` and `./gradlew bootRun`
3. The application is now live at http://localhost:8080/

## Comments
### What's new
* Integration tests with `MockWebServer` 
  
  This library allows to run a lightweight web server returning hard coded JSON responses. This makes it possible to test that services and clients work properly, without sending requests to Github API.
* Error handling with `@ControllerAdvice`
  * In case Github API returns `4xx client error` like `404` or `401`, a corresponding error code along with an error message is returned from my application
  * In case Github API returns `5xx server error`, the request is repeated at increasing intervals. If the error persists, `503 Service Unavailable` is returned.

### Concurrency
Initially, I made the requests synchronous.
However, fetching the languages for a user with lots of repos would take a long time because for each repository a separate request would have to be made to the GitHub API.

Implementing asynchronous request handling, where many requests are sent concurrently allowed to significantly reduce the processing time of the request.

**Technical details**
* I first gather the links to the languages api (for a given number of repositories) in an array, and then query GitHub using these links asynchronously.

### Further development
* Additional tests can be added ✔️
* Error handling ✔️

## API

### Repositories
Lists repos for a given user

http://localhost:8080/api/v1/users/{username}/repos?page=1&per_page=30

* `page` - (optional) number of page dispayed, 1 by default
* `per_page` - (optional) number of entries per page, 30 by default

Example:

* Request:
  http://localhost:8080/api/v1/users/microsoft/repos?page=1&per_page=3

* Response:

```json
{
  "repos": [
    {
      "name": ".github",
      "languages": {}
    },
    {
      "name": ".Net-Interactive-Kernels-ADS",
      "languages": {
        "JavaScript": 48809,
        "HTML": 31120,
        "C#": 25432,
        "PowerShell": 4169,
        "CSS": 2020
      }
    },
    {
      "name": ".NET-Modernization-In-a-Day",
      "languages": {
        "Jupyter Notebook": 5102,
        "TypeScript": 828
      }
    }
  ],
  "pagination": {
    "prevPage": null,
    "nextPage": "https://api.github.com/user/6154722/repos?per_page=3&page=2",
    "firstPage": null,
    "lastPage": "https://api.github.com/user/6154722/repos?per_page=3&page=1590"
  }
}
```


### User info
Shows information about given user:
* login, name, bio 
* most popular languages (name, number of bytes of code)

http://localhost:8080/api/v1/users/{username}

* `repos` - (optional) number of repositories to consider when counting languages used (taken alphabetically), 100 by default. ⚠️ If you intend to use large values keep in mind that for repos=`n`, more than `n` requests are sent, which may drain your GitHub quota quickly.

Example:

* Request:
  http://localhost:8080/api/v1/users/microsoft


* Response:

```json
{
  "login": "microsoft",
  "name": "Microsoft",
  "bio": "Open source projects and samples from Microsoft",
  "languages": {
    "languagesSortedDesc": {
      "HTML": 43682126,
      "Jupyter Notebook": 37399189,
      "C++": 18861280,
      "C#": 13036004,
      "TypeScript": 11688220,
      "Python": 5701741,
      "JavaScript": 4013562,
      "MATLAB": 2889825,
      "PowerShell": 2750768,
      "Java": 1717442,
      "C": 1530461,
      "Objective-C++": 963947,
      "CSS": 601688,
      "ABAP": 352899,
      "Shell": 313415,
      "Objective-C": 219104,
      "Go": 201537,
      "SCSS": 168178,
      "Julia": 161005,
      "CMake": 157460,
      "R": 125911,
      "MLIR": 121643,
      "TeX": 115205,
      "EJS": 97036,
      "Rich Text Format": 47649,
      "SWIG": 47312,
      "Kotlin": 44504,
      "Dockerfile": 33309,
      "Scala": 29552,
      "F#": 23875,
      "Makefile": 21559,
      "Batchfile": 18647,
      "ASP": 13629,
      "HCL": 13314,
      "Raku": 13069,
      "VBScript": 12957,
      "M": 8943,
      "Roff": 8135,
      "PLpgSQL": 5722,
      "Ruby": 5439,
      "Vue": 5113,
      "Mustache": 4593,
      "Swift": 2923,
      "TSQL": 2854,
      "ANTLR": 2459,
      "Bicep": 2116,
      "Smarty": 949,
      "Perl": 341,
      "NSIS": 165,
      "Pascal": 54,
      "Procfile": 16
    }
  }
}
```