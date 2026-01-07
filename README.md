# quarkus-mdc-bug

[![Build and Test](https://github.com/lloydmeta/quarkus-mdc-bug/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/lloydmeta/quarkus-mdc-bug/actions/workflows/build.yml)

Repro for new MDC (?) behaviour under 3.30.5
  * Bonus: Removing `WithSpan` in `SleepAuthMechanism#authenticate` will also make the test pass.

## Repro

1. Clone this repo
2. Run `./gradlew test` 💥
3. Update gradle.properties to have `quarkusPlatformVersion=3.30.4` instead
4. Run `./gradlew test` again to see it pass 🎉

## Bug

MDC data set in _Response_ filters are lost after `@WithSpan`-annotated methods (e.g. normal methods and/or on `HttpAuthenticationMechanism`(s)) are run

## Expected behaviour

MDC data set in _Response_ filters are retained after `@WithSpan`-annotated methods (e.g. normal methods and/or on `HttpAuthenticationMechanism`(s)) are run.

## Affected versions

Quarkus 3.30.5

## Works under

Quarkus =< 3.30.4

