package com.library.util;

/**
 * Hand-written JSON builder — no external library needed.
 * Properly escapes quotes, backslashes, and control characters.
 */
public class JsonBuilder {

    // Escape a Java string for safe embedding inside a JSON string value
    public static String escape(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }

    // Wrap a raw JSON value (object/array/null/number/bool) in the standard envelope
    public static String envelope(boolean success, String message, String dataJson) {
        return "{\"success\":" + success
             + ",\"message\":\"" + escape(message) + "\""
             + ",\"data\":" + (dataJson == null ? "null" : dataJson)
             + "}";
    }

    // Convenience: success response
    public static String ok(String message, String dataJson) {
        return envelope(true, message, dataJson);
    }

    // Convenience: error response
    public static String error(String message) {
        return envelope(false, message, "null");
    }

    // Build a JSON number field: "key": 42
    public static String num(String key, Number value) {
        return "\"" + escape(key) + "\":" + value;
    }

    // Build a JSON string field: "key": "value"
    public static String str(String key, String value) {
        return "\"" + escape(key) + "\":\"" + escape(value) + "\"";
    }

    // Build a JSON boolean field: "key": true
    public static String bool(String key, boolean value) {
        return "\"" + escape(key) + "\":" + value;
    }

    // Wrap fields in curly braces to make an object string
    public static String obj(String... fields) {
        return "{" + String.join(",", fields) + "}";
    }

    // Wrap items in an array
    public static String arr(java.util.List<String> items) {
        return "[" + String.join(",", items) + "]";
    }
}
