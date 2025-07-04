package com.auto.ht.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
    private static final Gson gson = new Gson();

    /**
     * Load JSON file and parse it into a specific class.
     */
    public static <T> T fromJsonFile(String filePath, Class<T> clazz) {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON file: {}", filePath, e);
            return null;
        }
    }

    /**
     * Load JSON file and parse it into a generic type (like List<T>).
     */
    public static <T> T fromJsonFile(String filePath, Type typeOfT) {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, typeOfT);
        } catch (Exception e) {
            log.error("Failed to parse JSON file: {}", filePath, e);
            return null;
        }
    }

    /**
     * Parse JSON string into an object.
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            log.error("Invalid JSON string", e);
            return null;
        }
    }

    /**
     * Parse JSON string into a generic type (e.g., List<T>).
     */
    public static <T> T fromJsonString(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            log.error("Invalid JSON string", e);
            return null;
        }
    }

    /**
     * Convert object to JSON string.
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Parse JSON file into JsonObject.
     */
    public static JsonObject parseToJsonObject(String filePath) {
        try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            log.error("Failed to parse JSON object from file: {}", filePath, e);
            return null;
        }
    }

    /**
     * Convert JsonObject to a typed object.
     */
    public static <T> T toObject(JsonObject jsonObject, Class<T> clazz) {
        return gson.fromJson(jsonObject, clazz);
    }

    /**
     * Extract a JSON array and convert to String[].
     */
    public static String[] extractStringArray(JsonObject obj, String key) {
        JsonArray array = obj.getAsJsonArray(key);
        if (array == null) return new String[0];

        List<String> result = new ArrayList<>();
        array.forEach(elem -> result.add(elem.getAsString()));
        return result.toArray(new String[0]);
    }

    /**
     * Generic method for parsing JSON arrays into list of objects.
     */
    public static <T> List<T> parseJsonArrayToList(String filePath, Class<T> elementClass) {
        Type listType = TypeToken.getParameterized(List.class, elementClass).getType();
        return fromJsonFile(filePath, listType);
    }
}
