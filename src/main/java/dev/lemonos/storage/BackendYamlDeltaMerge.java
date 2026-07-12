package dev.lemonos.storage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

final class BackendYamlDeltaMerge {
    private BackendYamlDeltaMerge() {
    }

    static Map<String, Object> merge(
            Map<String, Object> baseline,
            Map<String, Object> desired,
            Map<String, Object> current) {
        Map<String, Object> merged = new LinkedHashMap<>(current);
        for (String path : baseline.keySet()) {
            if (!desired.containsKey(path)) {
                merged.remove(path);
            }
        }
        for (Map.Entry<String, Object> entry : desired.entrySet()) {
            String path = entry.getKey();
            if (!baseline.containsKey(path) || !Objects.equals(baseline.get(path), entry.getValue())) {
                merged.put(path, entry.getValue());
            }
        }
        return merged;
    }
}
