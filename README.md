# Selenide Project
A robust Java-based test automation framework using Maven, Selenide, and TestNG. Designed for cross-browser, parallel, and distributed testing with advanced reporting and retry mechanisms.

## Table of Contents
1. [Features](#features)
2. [Setup](#setup)
3. [Running Tests](#running-tests)
4. [Test Configuration](#test-configuration)
5. [Reporting](#reporting)
6. [Test Retry Mechanism](#test-retry-mechanism)
7. [Parallel/Distributed Testing](#paralleldistributed-testing)

## Features
- [x] Selenide FW (JAVA 21, Selenide 7.9.3)
- [x] Reports with HTML, Allure and Report Portal (Allure and TestNG HTML)
- [x] Test retry (Retry maximum 2 times when failure)
- [x] Parallel/distributed testing (Using TestNG Parallel)
- [x] Cross browsers testing (Microsoft Edge, Google Chrome)
- [x] Multiple languages testing (Testing on Vietnamese and English)
- [ ] Selenium Grid/Shard
- [ ] CI
- [ ] Content testing
- [ ] Group tests by purposes
- [ ] Source control practice
- [ ] Switch test environment
- [ ] Wrap custom controls

## Setup
### Prerequisites
- Java 21 or higher
- Maven 3.8+
- Browsers: Google Chrome and Microsoft Edge

### Installation
Clone the repository and install dependencies:
```sh
git clone https://github.com/your-repo.git
cd your-repo
mvn clean install
```

## Running Tests
### Basic Test Execution
To run all tests, use the following command:
  ```sh
  mvn test
  ```

### Advanced Usage
- Run with specific browser:
  ```sh
  mvn test -Dbrowser=chrome
  ```
- Run in parallel:
  ```sh
  mvn test -Dparallel=true
  ```
## Test Configuration
Configuration is managed via `src/test/resources/config.properties` and Maven profiles. Adjust browser, environment, and language as needed.

## Reporting
- **HTML Report:** `target/surefire-reports/index.html`
- **Allure Report:**
  ```sh
  mvn allure:serve
  ```
- **Report Portal:** Integration available (see docs)

## Test Retry Mechanism
TestNG retry is enabled (max 2 times on failure). See `Retry.java` for logic.

## Parallel/Distributed Testing
Parallel execution is configured via TestNG XML and Maven Surefire plugin. Selenium Grid support is planned.

