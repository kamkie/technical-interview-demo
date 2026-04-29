package team.jit.technicalinterviewdemo.logging;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Principal;
import java.time.temporal.Temporal;
import java.util.*;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

    private static final String REDACTED = "<redacted>";
    private static final String OMITTED = "<omitted>";
    private static final int MAX_DEPTH = 2;
    private static final int MAX_COLLECTION_ITEMS = 10;
    private static final Set<String> SENSITIVE_TOKENS = Set.of(
            "password",
            "passwd",
            "pwd",
            "secret",
            "token",
            "authorization",
            "credential",
            "cookie",
            "session",
            "apikey",
            "accesskey",
            "privatekey",
            "clientsecret",
            "bearer",
            "refreshtoken"
    );

    @Around("@within(service)")
    public Object logServiceCall(ProceedingJoinPoint joinPoint, Service service) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String serviceName = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Map<String, Object> parameters = sanitizeArguments(signature.getParameterNames(), joinPoint.getArgs());
        long startTimeNanos = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            log.info(
                    "Service call {}.{} parameters={} durationMs={}",
                    serviceName,
                    methodName,
                    parameters,
                    toDurationMillis(startTimeNanos)
            );
            return result;
        } catch (Throwable exception) {
            log.info(
                    "Service call {}.{} parameters={} durationMs={} completedWithException=true",
                    serviceName,
                    methodName,
                    parameters,
                    toDurationMillis(startTimeNanos)
            );
            throw exception;
        }
    }

    private Map<String, Object> sanitizeArguments(String[] parameterNames, Object[] arguments) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();

        for (int index = 0; index < arguments.length; index++) {
            String parameterName = parameterNames != null && index < parameterNames.length
                    ? parameterNames[index]
                    : "arg" + index;
            sanitized.put(parameterName, sanitizeValue(parameterName, arguments[index], 0, visited));
        }

        return sanitized;
    }

    private Object sanitizeValue(String name, Object value, int depth, IdentityHashMap<Object, Boolean> visited) {
        if (isSensitive(name)) {
            return REDACTED;
        }
        if (value == null) {
            return null;
        }
        if (isSimpleValue(value)) {
            return value;
        }
        if (isInfrastructureType(value)) {
            return OMITTED;
        }
        if (depth >= MAX_DEPTH) {
            return "<" + value.getClass().getSimpleName() + ">";
        }
        if (visited.containsKey(value)) {
            return "<circular>";
        }

        visited.put(value, Boolean.TRUE);
        try {
            if (value.getClass().isArray()) {
                return sanitizeArray(value, depth, visited);
            }
            return switch (value) {
                case Collection<?> collection -> sanitizeCollection(collection, depth, visited);
                case Map<?, ?> map -> sanitizeMap(map, depth, visited);
                case MultipartFile file -> Map.of(
                        "name", file.getName(),
                        "originalFilename", file.getOriginalFilename(),
                        "contentType", file.getContentType(),
                        "size", file.getSize()
                );
                default -> sanitizeObjectFields(value, depth, visited);
            };
        } finally {
            visited.remove(value);
        }
    }

    private List<Object> sanitizeArray(Object array, int depth, IdentityHashMap<Object, Boolean> visited) {
        int length = Array.getLength(array);
        List<Object> items = new ArrayList<>(Math.min(length, MAX_COLLECTION_ITEMS));
        for (int index = 0; index < Math.min(length, MAX_COLLECTION_ITEMS); index++) {
            items.add(sanitizeValue("item", Array.get(array, index), depth + 1, visited));
        }
        if (length > MAX_COLLECTION_ITEMS) {
            items.add("... +" + (length - MAX_COLLECTION_ITEMS) + " more");
        }
        return items;
    }

    private List<Object> sanitizeCollection(Collection<?> collection, int depth, IdentityHashMap<Object, Boolean> visited) {
        List<Object> items = new ArrayList<>(Math.min(collection.size(), MAX_COLLECTION_ITEMS));
        int index = 0;
        for (Object item : collection) {
            if (index++ >= MAX_COLLECTION_ITEMS) {
                items.add("... +" + (collection.size() - MAX_COLLECTION_ITEMS) + " more");
                break;
            }
            items.add(sanitizeValue("item", item, depth + 1, visited));
        }
        return items;
    }

    private Map<String, Object> sanitizeMap(Map<?, ?> map, int depth, IdentityHashMap<Object, Boolean> visited) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (index++ >= MAX_COLLECTION_ITEMS) {
                sanitized.put("...", "+" + (map.size() - MAX_COLLECTION_ITEMS) + " more");
                break;
            }
            String key = String.valueOf(entry.getKey());
            sanitized.put(key, sanitizeValue(key, entry.getValue(), depth + 1, visited));
        }
        return sanitized;
    }

    private Object sanitizeObjectFields(Object value, int depth, IdentityHashMap<Object, Boolean> visited) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        for (Field field : getAllFields(value.getClass())) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);
            try {
                sanitized.put(field.getName(), sanitizeValue(field.getName(), field.get(value), depth + 1, visited));
            } catch (IllegalAccessException exception) {
                sanitized.put(field.getName(), "<inaccessible>");
            }
        }
        return sanitized.isEmpty() ? "<" + value.getClass().getSimpleName() + ">" : sanitized;
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    private boolean isSimpleValue(Object value) {
        return value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof CharSequence
                || value instanceof Enum<?>
                || value instanceof UUID
                || value instanceof Temporal;
    }

    private boolean isInfrastructureType(Object value) {
        return value instanceof ServletRequest
                || value instanceof ServletResponse
                || value instanceof Principal
                || value instanceof BindingResult
                || value instanceof InputStream
                || value instanceof OutputStream
                || value instanceof Reader
                || value instanceof Writer
                || value instanceof Resource;
    }

    private boolean isSensitive(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String normalized = normalize(name);
        for (String token : SENSITIVE_TOKENS) {
            if (normalized.contains(normalize(token))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    private long toDurationMillis(long startTimeNanos) {
        return (System.nanoTime() - startTimeNanos) / 1_000_000;
    }
}
