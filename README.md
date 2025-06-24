# Selenide Project
This project is a Java-based test automation framework using Maven, Selenide, and TestNG.

## Table of Contents
1. [Features](#features)
2. [Parallel/Distributed Testing](#paralleldistributed-testing)


## Features
| Feature                                     | Status                                           | Note                          |
|---------------------------------------------|--------------------------------------------------|-------------------------------|
| Selenide FW                                 | <span style="color: green;">Implemented</span>   | JAVA 21, Selenide 7.9.3       |
| Reports with HTML, Allure and Report Portal | <span style="color: cyan;">In - Progress</span>  | Allure and TestNG HTML        |
| Test retry                                  | <span style="color: cyan;">In - Progress</span>  | Retry 2 times when failure    |
| Parallel/distributed testing                | <span style="color: green;">Implemented</span>   | Using TestNG Parallel & Grid  |
| Cross browsers testing                      | <span style="color: green;">Implemented</span>   | Microsoft Edge, Google Chrome |
| Selenium Grid/Shard                         | <span style="color: red;">Not Implemented</span> |                               |
| CI                                          | <span style="color: red;">Not Implemented</span> |                               |
| Content testing                             | <span style="color: red;">Not Implemented</span> |                               |
| Multiple languages testing                  | <span style="color: cyan;">In - Progress</span>  |                               |
| Group tests by purposes                     | <span style="color: red;">Not Implemented</span> |                               |
| Source control practice                     | <span style="color: red;">Not Implemented</span> |                               |
| Switch test environment                     | <span style="color: red;">Not Implemented</span> |                               |
| Wrap custom controls                        | <span style="color: red;">Not Implemented</span> |                               |

### Report  (Temporary) 
HTML: xdg-open testng-report/index.html
Allure: mvn allure:serve

## Parallel/Distributed Testing

This framework supports distributed testing with Selenium Grid, allowing you to run tests in parallel across multiple browsers and machines.

### Features

- **Test Parallelization**: Run tests simultaneously across multiple browsers
- **Cross-Browser Testing**: Automated execution on Chrome and Edge browsers
- **Scalable Infrastructure**: Easy to add more browser nodes to the grid
- **Enhanced Reporting**: Detailed Allure reports with grid execution information

### Setup and Usage

The project includes a convenient script (`grid_runner.sh`) to manage all aspects of distributed testing:

#### 1. Starting Selenium Grid

```bash
./grid_runner.sh start
```

This command starts the Selenium Grid infrastructure using Docker:
- Selenium Hub on port 4444
- Chrome Node (5 concurrent sessions)
- Edge Node (5 concurrent sessions)

You can verify the grid is running by visiting: http://localhost:4444

#### 2. Running Tests in Distributed Mode

```bash
./grid_runner.sh run
```

This executes tests against the grid using the default `grid-test.xml` suite. You can also specify a custom suite:

```bash
./grid_runner.sh run custom-suite.xml
```

#### 3. Managing the Grid

```bash
# Check grid status
./grid_runner.sh status

# Stop the grid
./grid_runner.sh stop

# Clean up resources
./grid_runner.sh clean
```

### Configuration

The grid setup is defined in `src/main/resources/grid/docker-compose.yml`.

Tests running in grid mode use the TestNG suite configuration in `src/test/resources/suites/grid-test.xml`, which includes:

```xml
<suite name="Selenide Grid Test Suite" parallel="tests" thread-count="2">
    <test name="Chrome Tests" parallel="classes" thread-count="3">
        <parameter name="browser" value="chrome"/>
        <parameter name="gridEnabled" value="true"/>
        <parameter name="gridURL" value="http://localhost:4444/wd/hub"/>
        <!-- Test classes -->
    </test>
    
    <test name="Edge Tests" parallel="classes" thread-count="3">
        <!-- Similar configuration for Edge -->
    </test>
</suite>
```

### Benefits

1. **Faster Test Execution**: Run tests across multiple browsers simultaneously
2. **Better Resource Utilization**: Distribute test load across multiple machines
3. **Easier Test Maintenance**: Single codebase for multiple browsers
4. **Scalable Testing**: Add more nodes to handle increased test load
