package com.logicveda.marketplace.vendor.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class for JSON operations
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    /**
     * Convert object to JSON string
     */
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error converting object to JSON", e);
            return null;
        }
    }

    /**
     * Convert JSON string to object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error converting JSON to object", e);
            return null;
        }
    }

    /**
     * Convert JSON string to list
     */
    public <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            log.error("Error converting JSON to list", e);
            return null;
        }
    }

    /**
     * Convert object to pretty JSON string
     */
    public String toPrettyJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error converting object to pretty JSON", e);
            return null;
        }
    }

    /**
     * Merge two JSON objects
     */
    public <T> T mergeJson(String json1, String json2, Class<T> clazz) {
        try {
            T obj1 = fromJson(json1, clazz);
            T obj2 = fromJson(json2, clazz);

            // Merge objects
            return objectMapper.readerForUpdating(obj1).readValue(json2);
        } catch (Exception e) {
            log.error("Error merging JSON objects", e);
            return null;
        }
    }

    /**
     * Check if string is valid JSON
     */
    public boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deep copy object using JSON serialization
     */
    public <T> T deepCopy(T obj, Class<T> clazz) {
        try {
            String json = toJson(obj);
            return fromJson(json, clazz);
        } catch (Exception e) {
            log.error("Error deep copying object", e);
            return null;
        }
    }
}
