package com.auto.ht.helpers;

import com.auto.ht.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Helper class for project-related operations
 * Handles project detection, URL management and project-specific configurations
 */
public class ProjectHelper {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    /**
     * Gets the current project name from selenide.properties file
     * @return The project name (e.g., "VjAir", "Agoda", "LF")
     */
    public static String getCurrentProject() {
        String projectName = System.getProperty("projectName");

        // If not found in system properties, then fall back to properties file
        if (projectName == null || projectName.isEmpty()) {
            projectName = LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, "projectName", "VjAir");
        }

        return projectName;
    }

    /**
     * Checks if the current project matches the specified project
     * @param projectName The project name to check against
     * @return true if current project matches, false otherwise
     */
    public static boolean isProject(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return false;
        }

        String currentProject = getCurrentProject().toLowerCase();
        projectName = projectName.toLowerCase();

        // Handle aliases for projects
        return switch (projectName) {
            case "vjair", "vj", "vietjet" -> currentProject.equals("vjair") ||
                                             currentProject.equals("vj") ||
                                             currentProject.equals("vietjet");
            case "leapfrog", "lf" -> currentProject.equals("leapfrog") ||
                                     currentProject.equals("lf");
            case "agoda" -> currentProject.equals("agoda");
            default -> currentProject.equals(projectName);
        };
    }

    /**
     * Gets the base URL for the current project
     * @return The base URL configured for the current project
     */
    public static String getBaseUrl() {
        String projectName = getCurrentProject().toLowerCase();

        return switch (projectName) {
            case "vjair", "vj", "vietjet" -> Constants.BASE_URL_VJ;
            case "leapfrog", "lf" -> Constants.BASE_URL_LF;
            case "agoda" -> Constants.BASE_URL_AGODA;
            default -> {
                log.warn("Unknown project name: {}. Defaulting to Vietjet base URL.", projectName);
                yield Constants.BASE_URL_VJ;
            }
        };
    }

    /**
     * Gets project-specific configuration value
     * @param key The configuration key
     * @param defaultValue The default value if key not found
     * @return The configuration value specific to current project
     */
    public static String getProjectConfig(String key, String defaultValue) {
        String projectName = getCurrentProject();
        String projectSpecificKey = projectName + "." + key;

        return LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, projectSpecificKey, defaultValue);
    }
}
