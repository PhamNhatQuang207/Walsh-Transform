# My Library

Minimal Java library skeleton (Maven).

Quick commands:

- Build: `mvn -B package`
- Test: `mvn -B test`
- Install to local repo: `mvn -B install`

Layout:

- `src/main/java` - library sources
- `src/test/java` - unit tests

Next steps to publish:

- Configure `distributionManagement` in `pom.xml` and GPG signing
- Create OSSRH account for Maven Central or configure private repository
