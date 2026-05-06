package team.jit.technicalinterviewdemo.technical.docs;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class OpenApiContractCompatibilityChecker {

    private OpenApiContractCompatibilityChecker() {
    }

    static List<String> findBreakingChanges(JsonNode approved, JsonNode current) {
        List<String> issues = new ArrayList<>();
        comparePaths(approved.path("paths"), current.path("paths"), approved.path("components"), current.path("components"), issues);
        return issues;
    }

    private static void comparePaths(
                                     JsonNode approvedPaths, JsonNode currentPaths, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues
    ) {
        Iterator<String> pathNames = approvedPaths.fieldNames();
        while (pathNames.hasNext()) {
            String pathName = pathNames.next();
            JsonNode approvedPath = approvedPaths.path(pathName);
            JsonNode currentPath = currentPaths.path(pathName);
            if (currentPath.isMissingNode()) {
                issues.add("Missing path: " + pathName);
                continue;
            }

            Iterator<String> methods = approvedPath.fieldNames();
            while (methods.hasNext()) {
                String method = methods.next();
                JsonNode approvedOperation = approvedPath.path(method);
                JsonNode currentOperation = currentPath.path(method);
                if (currentOperation.isMissingNode()) {
                    issues.add("Missing operation: " + method.toUpperCase(Locale.ROOT) + " " + pathName);
                    continue;
                }
                compareOperation(
                        pathName, method, approvedOperation, currentOperation, approvedComponents, currentComponents, issues
                );
            }
        }
    }

    private static void compareOperation(
                                         String pathName, String method, JsonNode approvedOperation, JsonNode currentOperation, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues
    ) {
        String operationLabel = method.toUpperCase(Locale.ROOT) + " " + pathName;
        compareSecurity(operationLabel, approvedOperation.path("security"), currentOperation.path("security"), issues);
        compareParameters(
                operationLabel, approvedOperation.path("parameters"), currentOperation.path("parameters"), approvedComponents, currentComponents, issues
        );
        compareRequestBody(
                operationLabel, approvedOperation.path("requestBody"), currentOperation.path("requestBody"), approvedComponents, currentComponents, issues
        );
        compareResponses(
                operationLabel, approvedOperation.path("responses"), currentOperation.path("responses"), approvedComponents, currentComponents, issues
        );
    }

    private static void compareSecurity(String operationLabel, JsonNode approvedSecurity, JsonNode currentSecurity, List<String> issues) {
        boolean approvedSecured = approvedSecurity.isArray() && !approvedSecurity.isEmpty();
        boolean currentSecured = currentSecurity.isArray() && !currentSecurity.isEmpty();

        if (!approvedSecured || !currentSecured) {
            if (!approvedSecured && currentSecured) {
                issues.add("Operation became secured: " + operationLabel);
            }
            return;
        }

        Set<String> approvedSchemes = securitySchemeNames(approvedSecurity.get(0));
        Set<String> currentSchemes = securitySchemeNames(currentSecurity.get(0));
        if (!currentSchemes.containsAll(approvedSchemes)) {
            issues.add("Operation changed security requirements: " + operationLabel);
        }
    }

    private static Set<String> securitySchemeNames(JsonNode securityRequirement) {
        Set<String> schemes = new LinkedHashSet<>();
        Iterator<String> fieldNames = securityRequirement.fieldNames();
        while (fieldNames.hasNext()) {
            schemes.add(fieldNames.next());
        }
        return schemes;
    }

    private static void compareParameters(
                                          String operationLabel, JsonNode approvedParameters, JsonNode currentParameters, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues
    ) {
        Map<String, JsonNode> approvedParameterMap = indexParameters(approvedParameters);
        Map<String, JsonNode> currentParameterMap = indexParameters(currentParameters);

        for (Map.Entry<String, JsonNode> entry : approvedParameterMap.entrySet()) {
            JsonNode currentParameter = currentParameterMap.get(entry.getKey());
            if (currentParameter == null) {
                issues.add("Missing parameter on " + operationLabel + ": " + entry.getKey());
                continue;
            }

            boolean approvedRequired = entry.getValue().path("required").asBoolean(false);
            boolean currentRequired = currentParameter.path("required").asBoolean(false);
            if (!approvedRequired && currentRequired) {
                issues.add("Parameter became required on " + operationLabel + ": " + entry.getKey());
            }

            compareSchema(
                    operationLabel + " parameter " + entry.getKey(), entry.getValue().path("schema"), currentParameter.path("schema"), approvedComponents, currentComponents, issues, true
            );
        }

        for (Map.Entry<String, JsonNode> entry : currentParameterMap.entrySet()) {
            if (!approvedParameterMap.containsKey(entry.getKey()) && entry.getValue().path("required").asBoolean(false)) {
                issues.add("New required parameter on " + operationLabel + ": " + entry.getKey());
            }
        }
    }

    private static Map<String, JsonNode> indexParameters(JsonNode parametersNode) {
        Map<String, JsonNode> parameters = new LinkedHashMap<>();
        if (!parametersNode.isArray()) {
            return parameters;
        }
        for (JsonNode parameter : parametersNode) {
            String key = parameter.path("in").asText() + ":" + parameter.path("name").asText();
            parameters.put(key, parameter);
        }
        return parameters;
    }

    private static void compareRequestBody(
                                           String operationLabel, JsonNode approvedRequestBody, JsonNode currentRequestBody, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues
    ) {
        if (approvedRequestBody.isMissingNode()) {
            if (currentRequestBody.path("required").asBoolean(false)) {
                issues.add("Operation gained a required request body: " + operationLabel);
            }
            return;
        }
        if (currentRequestBody.isMissingNode()) {
            issues.add("Missing request body: " + operationLabel);
            return;
        }

        boolean approvedRequired = approvedRequestBody.path("required").asBoolean(false);
        boolean currentRequired = currentRequestBody.path("required").asBoolean(false);
        if (!approvedRequired && currentRequired) {
            issues.add("Request body became required: " + operationLabel);
        }

        compareContentSchemas(
                operationLabel + " request body", approvedRequestBody.path("content"), currentRequestBody.path("content"), approvedComponents, currentComponents, issues, true
        );
    }

    private static void compareResponses(
                                         String operationLabel, JsonNode approvedResponses, JsonNode currentResponses, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues
    ) {
        Iterator<String> statusCodes = approvedResponses.fieldNames();
        while (statusCodes.hasNext()) {
            String statusCode = statusCodes.next();
            JsonNode approvedResponse = approvedResponses.path(statusCode);
            JsonNode currentResponse = currentResponses.path(statusCode);
            if (currentResponse.isMissingNode()) {
                issues.add("Missing response " + statusCode + " on " + operationLabel);
                continue;
            }

            compareContentSchemas(
                    operationLabel + " response " + statusCode, approvedResponse.path("content"), currentResponse.path("content"), approvedComponents, currentComponents, issues, false
            );
        }
    }

    private static void compareContentSchemas(
                                              String label, JsonNode approvedContent, JsonNode currentContent, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues, boolean requestContext
    ) {
        if (approvedContent.isMissingNode() || approvedContent.isEmpty()) {
            return;
        }

        Iterator<String> mediaTypes = approvedContent.fieldNames();
        while (mediaTypes.hasNext()) {
            String mediaType = mediaTypes.next();
            JsonNode approvedSchema = approvedContent.path(mediaType).path("schema");
            JsonNode currentSchema = currentContent.path(mediaType).path("schema");
            if (currentSchema.isMissingNode()) {
                issues.add("Missing media type " + mediaType + " for " + label);
                continue;
            }
            compareSchema(label + " " + mediaType, approvedSchema, currentSchema, approvedComponents, currentComponents, issues, requestContext);
        }
    }

    private static void compareSchema(
                                      String label, JsonNode approvedSchema, JsonNode currentSchema, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues, boolean requestContext
    ) {
        JsonNode resolvedApproved = resolveSchema(approvedSchema, approvedComponents);
        JsonNode resolvedCurrent = resolveSchema(currentSchema, currentComponents);

        if (resolvedApproved.isMissingNode()) {
            return;
        }
        if (resolvedCurrent.isMissingNode()) {
            issues.add("Missing schema for " + label);
            return;
        }

        String approvedType = resolvedApproved.path("type").asText("");
        String currentType = resolvedCurrent.path("type").asText("");
        if (!approvedType.isBlank() && !currentType.isBlank() && !approvedType.equals(currentType)) {
            issues.add("Schema type changed for " + label + ": " + approvedType + " -> " + currentType);
            return;
        }

        compareEnums(label, resolvedApproved, resolvedCurrent, issues);

        if ("object".equals(approvedType) || resolvedApproved.has("properties")) {
            compareObjectSchema(label, resolvedApproved, resolvedCurrent, approvedComponents, currentComponents, issues, requestContext);
            return;
        }

        if ("array".equals(approvedType)) {
            compareSchema(
                    label + "[]", resolvedApproved.path("items"), resolvedCurrent.path("items"), approvedComponents, currentComponents, issues, requestContext
            );
        }
    }

    private static void compareEnums(String label, JsonNode approvedSchema, JsonNode currentSchema, List<String> issues) {
        if (!approvedSchema.has("enum")) {
            return;
        }
        Set<String> approvedValues = arrayValues(approvedSchema.path("enum"));
        Set<String> currentValues = arrayValues(currentSchema.path("enum"));
        if (!currentValues.containsAll(approvedValues)) {
            issues.add("Enum values were removed from " + label);
        }
    }

    private static Set<String> arrayValues(JsonNode arrayNode) {
        Set<String> values = new LinkedHashSet<>();
        if (!arrayNode.isArray()) {
            return values;
        }
        for (JsonNode value : arrayNode) {
            values.add(value.asText());
        }
        return values;
    }

    private static void compareObjectSchema(
                                            String label, JsonNode approvedSchema, JsonNode currentSchema, JsonNode approvedComponents, JsonNode currentComponents, List<String> issues, boolean requestContext
    ) {
        JsonNode approvedProperties = approvedSchema.path("properties");
        JsonNode currentProperties = currentSchema.path("properties");

        Iterator<String> propertyNames = approvedProperties.fieldNames();
        while (propertyNames.hasNext()) {
            String propertyName = propertyNames.next();
            JsonNode currentProperty = currentProperties.path(propertyName);
            if (currentProperty.isMissingNode()) {
                issues.add("Missing property on " + label + ": " + propertyName);
                continue;
            }
            compareSchema(
                    label + "." + propertyName, approvedProperties.path(propertyName), currentProperty, approvedComponents, currentComponents, issues, requestContext
            );
        }

        if (requestContext) {
            Set<String> approvedRequired = arrayValues(approvedSchema.path("required"));
            Set<String> currentRequired = arrayValues(currentSchema.path("required"));
            for (String requiredProperty : currentRequired) {
                if (!approvedRequired.contains(requiredProperty)) {
                    issues.add("Request schema added a required property on " + label + ": " + requiredProperty);
                }
            }
        }
    }

    private static JsonNode resolveSchema(JsonNode schema, JsonNode components) {
        if (schema.isMissingNode()) {
            return schema;
        }
        JsonNode currentSchema = schema;
        while (currentSchema.has("$ref")) {
            String ref = currentSchema.path("$ref").asText();
            if (!ref.startsWith("#/components/schemas/")) {
                return currentSchema;
            }
            String schemaName = ref.substring("#/components/schemas/".length());
            currentSchema = components.path("schemas").path(schemaName);
        }
        return currentSchema;
    }
}
