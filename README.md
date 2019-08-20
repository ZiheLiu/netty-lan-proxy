# JavaMavenTemplate

[![Build Status](https://travis-ci.org/ZiheLiu/JavaMavenTeamplate.svg?branch=master)](https://travis-ci.org/ZiheLiu/JavaMavenTeamplate)

A template project based on java and maven, so as to start a new project quickly.



## Dependencies

- `maven` > 3.0
- `java` > 8.0




## TODO

- [x] Use `Junit` to test.
- [x] Use `checkstyle` to checkstyle.
- [x] Use `maven-compiler-plugin` to set source and target version of java.
- [x] Use `maven-shade-plugin` to set mainClass of project.
- [x] Use `travis` to build after push to GitHub.

## Usages

```shell
$ mvn clean test

$ mvn clean package

$ java -jar target/<project-name>.jar
```