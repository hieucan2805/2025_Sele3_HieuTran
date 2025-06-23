#!/bin/bash

# Script to manage Selenium Grid and run distributed tests
# Author: GitHub Copilot for Hieu Tran's Selenide Project

GRID_DIR="$(pwd)/src/main/resources/grid"
COMPOSE_FILE="$GRID_DIR/docker-compose.yml"

# Function to display usage information
usage() {
    echo "Selenium Grid Management Script"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  start    - Start Selenium Grid infrastructure"
    echo "  stop     - Stop Selenium Grid infrastructure"
    echo "  status   - Check Selenium Grid status"
    echo "  run      - Run tests against Selenium Grid"
    echo "  clean    - Stop grid and clean up resources"
    echo "  help     - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start                  - Start Selenium Grid"
    echo "  $0 run                    - Run tests using grid-test.xml"
    echo "  $0 run customSuite.xml    - Run custom suite against grid"
    echo ""
}

# Function to start Selenium Grid
start_grid() {
    echo "Starting Selenium Grid infrastructure..."

    # Check if Docker is running
    if ! docker info > /dev/null 2>&1; then
        echo "Error: Docker is not running. Please start Docker and try again."
        exit 1
    fi

    # Navigate to the grid directory and start containers
    cd "$GRID_DIR" || { echo "Error: Grid directory not found"; exit 1; }
    docker-compose -f "$COMPOSE_FILE" up -d

    echo "Waiting for Grid to be fully up and running..."
    sleep 5

    # Check if grid is available
    MAX_RETRIES=10
    RETRY_COUNT=0

    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        if curl -s http://localhost:4444/wd/hub/status | grep -q "\"ready\":true"; then
            echo "Selenium Grid is up and running!"
            echo "Grid Console URL: http://localhost:4444"
            return 0
        fi

        echo "Grid not ready yet. Retrying in 2 seconds..."
        sleep 2
        RETRY_COUNT=$((RETRY_COUNT + 1))
    done

    echo "Error: Selenium Grid failed to start properly within the timeout period."
    echo "Check 'docker-compose logs' for more details."
    return 1
}

# Function to stop Selenium Grid
stop_grid() {
    echo "Stopping Selenium Grid..."
    cd "$GRID_DIR" || { echo "Error: Grid directory not found"; exit 1; }
    docker-compose -f "$COMPOSE_FILE" down
    echo "Selenium Grid has been stopped."
}

# Function to check Selenium Grid status
check_status() {
    if curl -s http://localhost:4444/wd/hub/status | grep -q "\"ready\":true"; then
        echo "Selenium Grid is running and ready for tests."
        echo "Grid Console URL: http://localhost:4444"
    else
        echo "Selenium Grid is not running or not ready."
    fi
}

# Function to run tests against the grid
run_tests() {
    local suite_file="${1:-grid-test.xml}"

    # Check if grid is available before running tests
    if ! curl -s http://localhost:4444/wd/hub/status | grep -q "\"ready\":true"; then
        echo "Error: Selenium Grid is not running or not ready."
        echo "Please start the grid first with '$0 start'"
        exit 1
    fi

    echo "Running tests against Selenium Grid using suite: $suite_file"
    mvn clean test -DsuiteXmlFile="$suite_file" -Dgrid.enabled=true

    # Generate Allure report
    echo "Generating Allure report..."
    mvn allure:report

    echo "Test execution complete. Allure report is available at: target/site/allure-maven-plugin/"
}

# Function to clean up resources
clean_up() {
    echo "Cleaning up Selenium Grid resources..."
    stop_grid
    echo "Removing unused Docker resources..."
    docker system prune -f
    echo "Cleanup complete."
}

# Main script logic
case "$1" in
    start)
        start_grid
        ;;
    stop)
        stop_grid
        ;;
    status)
        check_status
        ;;
    run)
        run_tests "$2"
        ;;
    clean)
        clean_up
        ;;
    help|*)
        usage
        ;;
esac
