package com.auto.ht.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * TestNG listener that handles cleanup of temporary resources created during test execution
 */
public class TestCleanupListener implements ITestListener {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());
    private static final ConcurrentHashMap<String, String> tempDirectories = new ConcurrentHashMap<>();
    private static final Set<String> registeredShutdownHooks = new HashSet<>();
    
    // Static initialization block to register a JVM shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM shutdown hook executed - cleaning up any remaining temporary directories");
            cleanupAllTempDirectories();
        }));
    }
    
    /**
     * Register a temporary user data directory for cleanup
     * 
     * @param testId A unique identifier for the test (can be class name, method name, etc.)
     * @param directoryPath Path to the temporary directory to clean up
     */
    public static void registerTempDirectory(String testId, String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return;
        }
        
        log.debug("Registered temp directory for cleanup: {}", directoryPath);
        tempDirectories.put(testId, directoryPath);
        
        // Register a test-specific shutdown hook if not already registered
        String hookId = "cleanup-" + testId;
        if (!registeredShutdownHooks.contains(hookId)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                cleanupTempDirectory(directoryPath);
                tempDirectories.remove(testId);
            }));
            registeredShutdownHooks.add(hookId);
        }
    }
    
    /**
     * Clean up a specific temporary directory
     * 
     * @param tempDirPath Path to the temporary directory
     */
    public static void cleanupTempDirectory(String tempDirPath) {
        if (tempDirPath == null || tempDirPath.isEmpty()) {
            return;
        }
        
        // Check if cleanup is disabled via system property
        if (Boolean.parseBoolean(System.getProperty("selenide.disableCleanup", "false"))) {
            log.info("Cleanup disabled by configuration. Skipping cleanup for: {}", tempDirPath);
            return;
        }

        try {
            Path userDataDirPath = Paths.get(tempDirPath);
            if (Files.exists(userDataDirPath)) {
                log.info("Cleaning up temporary directory: {}", tempDirPath);
                try (Stream<Path> pathStream = Files.walk(userDataDirPath)) {
                    pathStream.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(file -> {
                                boolean deleted = file.delete();
                                if (!deleted) {
                                    log.warn("Failed to delete file: {}", file.getAbsolutePath());
                                }
                            });
                }
                log.info("Successfully cleaned up directory: {}", tempDirPath);
            }
        } catch (Exception e) {
            log.error("Failed to clean up directory: {}", tempDirPath, e);
        }
    }
    
    /**
     * Clean up all registered temporary directories
     */
    public static void cleanupAllTempDirectories() {
        tempDirectories.forEach((testId, path) -> {
            cleanupTempDirectory(path);
            tempDirectories.remove(testId);
        });
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        cleanupTestResources(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        cleanupTestResources(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        cleanupTestResources(result);
    }

    @Override
    public void onFinish(ITestContext context) {
        // Final cleanup after all tests in the context have finished
        cleanupAllTempDirectories();
    }
    
    private void cleanupTestResources(ITestResult result) {
        String testId = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
        String tempDir = tempDirectories.get(testId);
        if (tempDir != null) {
            cleanupTempDirectory(tempDir);
            tempDirectories.remove(testId);
        }
    }
}
