# Selenide Project
This project is a Java-based test automation framework using Maven, Selenide, and TestNG.

## Table of Contents
1. [Features](#features)
2. [Parallel/Distributed Testing](#paralleldistributed-testing)


## Features
| Feature                                     | Status                                            | Note                               |
|---------------------------------------------|---------------------------------------------------|------------------------------------|
| Selenide FW                                 | <span style="color: green;">Implemented</span>    | JAVA 21, Selenide 7.9.3            |
| Reports with HTML, Allure and Report Portal | <span style="color: cyan;">In - Progress</span>   | Allure and TestNG HTML             |
| Test retry                                  | <span style="color: cyan;">In - Progress</span>   | Retry maximum 2 times when failure |
| Parallel/distributed testing                | <span style="color: cyan;">In - Progress</span>   | Using TestNG Parallel              |
| Cross browsers testing                      | <span style="color: green;">Implemented</span>    | Microsoft Edge, Google Chrome      |
| Selenium Grid/Shard                         | <span style="color: red;">Not Implemented</span>  |                                    |
| CI                                          | <span style="color: red;">Not Implemented</span>  |                                    |
| Content testing                             | <span style="color: red;">Not Implemented</span>  |                                    |
| Multiple languages testing                  | <span style="color: cyan;">In - Progress</span>   | Testing on Vietnamese and English  |
| Group tests by purposes                     | <span style="color: red;">Not Implemented</span>  |                                    |
| Source control practice                     | <span style="color: red;">Not Implemented</span>  |                                    |
| Switch test environment                     | <span style="color: red;">Not Implemented</span>  |                                    |
| Wrap custom controls                        | <span style="color: red;">Not Implemented</span>  |                                    |

## Selenide FW


## Report  (Temporary) 
HTML: xdg-open testng-report/index.html
Allure: mvn allure:serve

## Test retry
Test retry is implemented using TestNG's `@Test(retryAnalyzer = Retry.class)` annotation. The `Retry` class extends `IRetryAnalyzer` and overrides the `retry` method to determine if a test should be retried.