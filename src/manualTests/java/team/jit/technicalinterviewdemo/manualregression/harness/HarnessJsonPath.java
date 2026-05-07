package team.jit.technicalinterviewdemo.manualregression.harness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;

import java.util.List;

/** Minimal JSON-path helper for the manual-regression suite expressions. */
public final class HarnessJsonPath {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final JsonNode root;

    HarnessJsonPath(String body) {
        this.root = parse(body);
    }

    public Object get(String path) {
        JsonNode node = resolve(path);
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(node, Object.class);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path) {
        Object value = get(path);
        if (value instanceof List<?> list) {
            return (List<T>) list;
        }
        return List.of();
    }

    public String getString(String path) {
        Object value = get(path);
        return value == null ? null : String.valueOf(value);
    }

    public <T> T getObject(String path, Class<T> type) {
        Object value = get(path);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return OBJECT_MAPPER.convertValue(value, type);
    }

    private static JsonNode parse(String body) {
        if (body == null || body.isBlank()) {
            return NullNode.getInstance();
        }
        try {
            return OBJECT_MAPPER.readTree(body);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Response body is not valid JSON", ex);
        }
    }

    private JsonNode resolve(String path) {
        if (path == null || path.isBlank() || "$".equals(path)) {
            return root;
        }
        JsonNode current = root;
        int index = 0;
        while (index < path.length()) {
            if (current == null || current.isMissingNode() || current.isNull()) {
                return MissingNode.getInstance();
            }
            char currentChar = path.charAt(index);
            if (currentChar == '.') {
                index++;
            } else if (currentChar == '[') {
                int close = path.indexOf(']', index);
                if (close < 0) {
                    throw new IllegalArgumentException("Invalid JSON path: " + path);
                }
                int arrayIndex = Integer.parseInt(path.substring(index + 1, close));
                current = current.path(arrayIndex);
                index = close + 1;
            } else {
                int end = index;
                while (end < path.length() && path.charAt(end) != '.' && path.charAt(end) != '[') {
                    end++;
                }
                current = current.path(path.substring(index, end));
                index = end;
            }
        }
        return current;
    }
}
