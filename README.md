# Selenide Project
This project is built using Maven and designed to run automated UI tests using Selenide. It includes dependencies for logging, reporting, and test execution with TestNG, Allure, and SLF4J.

## Table of Contents
1. [Features](#features)


## Features
| Feature                                     | Status                                              | Note                          |
|---------------------------------------------|-----------------------------------------------------|-------------------------------|
| Selenide FW                                 | <span style="color: cyan;">In - Progress</span>     | JAVA 21, Selenide 7.9.3       |
| Reports with HTML, Allure and Report Portal | <span style="color: cyan;">In - Progress</span>     |                               |
| Test retry                                  | <span style="color: cyan;">In - Progress</span>     |                               |
| Parallel/distributed testing                | <span style="color: red;">Not Implemented</span>    |                               |
| Cross browsers testing                      | <span style="color: cyan;">In - Progress</span>     | Microsoft Edge, Google Chorme |
| Selenium Grid/Shard                         | <span style="color: red;">Not Implemented</span>    |                               |
| CI                                          | <span style="color: red;">Not Implemented</span>    |                               |
| Content testing                             | <span style="color: red;">Not Implemented</span>    |                               |
| Multiple languages testing                  | <span style="color: cyan;">In - Progress</span>     |                               |
| Group tests by purposes                     | <span style="color: red;">Not Implemented</span>    |                               |
| Source control practice                     | <span style="color: red;">Not Implemented</span>    |                               |
| Switch test environment                     | <span style="color: red;">Not Implemented</span>    |                               |
| Wrap custom controls                        | <span style="color: red;">Not Implemented</span>    |                               |
                                                                              | Test cases use Page objects' methods instead of WebDriver's methods directly                                                                                                                                                    | <span style="color: red;">Not Implemented</span>    |



### Selenide FW

### Templates Report    
HTML: xdg-open testng-report/index.html
Allure: mvn allure:serve