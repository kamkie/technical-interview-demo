package team.jit.technicalinterviewdemo.technical.docs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class OpenApiContractSupport {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    private OpenApiContractSupport() {
    }

    static JsonNode normalize(String openApiJson) throws JsonProcessingException {
        JsonNode root = OBJECT_MAPPER.readTree(openApiJson);
        if (root instanceof ObjectNode rootObject) {
            rootObject.remove("servers");
        }
        JsonNode infoNode = root.path("info");
        if (infoNode instanceof ObjectNode infoObject) {
            infoObject.put("version", "APPROVED");
        }
        return root;
    }

    static String normalizeToPrettyJson(String openApiJson) throws JsonProcessingException {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(normalize(openApiJson));
    }

    static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }
}
