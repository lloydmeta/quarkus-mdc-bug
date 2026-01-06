# quarkus-mdc-bug

Repro for new MDC (?) behaviour under 3.30.5
  * Bonus: Removing `WithSpan` in `SleepAuthMechanism#authenticate` will also make the test pass.

## Repro

1. Clone this repo
2. Run `./gradlew test` 💥
3. Update gradle.properties to have `quarkusPlatformVersion=3.30.4` instead
4. Run `./gradlew test` again to see it pass 🎉

## Bug

MDC data set in _Response_ filters are lost after `HttpAuthenticationMechanism`(s) are run

## Expected behaviour

MDC data set in _Response_ filters are returned after `HttpAuthenticationMechanism`(s) are run.

## Affected versions

Quarkus 3.30.5

## Works under

Quarkus =< 3.30.4

