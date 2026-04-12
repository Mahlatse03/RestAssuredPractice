# RestAssuredPractice

Lightweight Maven project with Rest-Assured tests and small request/payload builders used for API automation practice.

## Purpose

This repository contains examples and tests that exercise a REST API using Rest-Assured. It includes request builders, payload builders, and example JUnit tests for common user flows (register, login, approve).

## Prerequisites

- Java 11 or newer (JDK 11+ recommended)
- Maven 3.6+ installed and available on PATH
- (Optional) An IDE such as IntelliJ IDEA for editing, running and debugging tests

Verify your environment:

```bash
java -version
mvn -version
```

## Build & quick checks

From the project root, run:

```bash
# compile test and main code
mvn test-compile

# run all tests
mvn test

# rebuild clean
mvn clean test
```

If you only want to compile without running tests:

```bash
mvn -DskipTests=true clean install
```

## Project structure (key files)

- pom.xml — Maven configuration and dependencies
- src/main/java — (empty or main source code; not used heavily in this practice project)
- src/test/java — test sources and helpers
  - payloadBuilder/registerUserPayload.java — builds JSON payloads for register/login
  - requestBuilder/UserRequestBuilder.java — Rest-Assured request builder for user-related endpoints
  - requestBuilder/User.java — small POJO added to represent login payloads (test scope)
  - test/UserTests.java — example JUnit test class using Faker and the request builders


## Recent fixes (why things changed)

While preparing the project, a few issues prevented the IDE from offering auto-imports and produced a compile error:

- `UserRequestBuilder` referenced a payload builder (from `payloadBuilder.registerUserPayload`) but the project also had an unresolved symbol `User` used as a method parameter. The unresolved `User` prevented helpful editor suggestions.
- To resolve this quickly, `payloadBuilder.registerUserPayload` was explicitly imported in `src/test/java/requestBuilder/UserRequestBuilder.java`.
- A small test-scoped POJO `src/test/java/requestBuilder/User.java` was added so the `userLogin(User)` signature compiles.

These changes are minimally invasive and intended to get the project compiling; you can follow the recommended next steps below to further clean up and standardize the code.

## Recommended next steps / cleanup

1. Rename `registerUserPayload` to follow Java naming conventions (e.g., `RegisterUserPayload`) and update imports/usages. This makes the code consistent and easier for the IDE to work with.
2. Mark `src/test/java` as the Test Sources Root in your IDE (IntelliJ: right-click the folder -> Mark Directory as -> Test Sources Root). This ensures the editor indexes test classes and offers imports.
3. Re-index the IDE if auto-import still doesn't appear: File -> Invalidate Caches / Restart (IntelliJ).
4. Remove or use unused local variables in `UserRequestBuilder` (there are `apiPath` variables declared but not used in a few methods) to clear warnings.
5. Run a full compile/test cycle:

```bash
mvn -DskipTests=false clean test-compile
mvn test
```

## How to run a single test

You can run a single test class with Maven:

```bash
mvn -Dtest=test.UserTests test
```

Or from IntelliJ, open the test file and use the gutter run icons to run or debug.

## Troubleshooting

- If imports are not suggested for classes under `src/test/java`, ensure the directory is marked as a Test Sources Root and re-index.
- If you see unresolved symbols after recent file moves/renames, re-run `mvn test-compile` to surface compiler errors.

## Contact / Ownership

This repository is a practice project. If you'd like, I can:
- Rename classes to follow Java conventions across the repo.
- Clean up the unused variable warnings and unused methods.
- Add a sample GitHub Actions workflow to run tests on push.

---
README generated and includes notes about the recent fixes to `UserRequestBuilder` and the added `User` POJO.
