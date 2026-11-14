package com.library.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses application/x-www-form-urlencoded request bodies and URL query strings.
 */
public class FormParser {

    // Parse a URL query string: "key1=val1&key2=val2"
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
                String val = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                params.put(key, val);
            }
        }
        return params;
    }

    // Read the request body as a string, then parse it as form-urlencoded
    public static Map<String, String> parseBody(InputStream body) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return parseQuery(sb.toString());
    }

    // Safe get: returns empty string if key is absent
    public static String get(Map<String, String> params, String key) {
        return params.getOrDefault(key, "").trim();
    }
}
