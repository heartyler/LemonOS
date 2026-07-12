package dev.lemonos.storage;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BackendYamlStoreHarness {
    private BackendYamlStoreHarness() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> baseline = map(
                "shared", "keep",
                "servers.lobby", "idle",
                "servers.survival", "idle");
        Map<String, Object> firstDesired = new LinkedHashMap<>(baseline);
        firstDesired.put("servers.lobby", "ready");
        Map<String, Object> afterFirst = BackendYamlDeltaMerge.merge(baseline, firstDesired, baseline);

        Map<String, Object> secondDesired = new LinkedHashMap<>(baseline);
        secondDesired.put("servers.survival", "sleeping");
        Map<String, Object> merged = BackendYamlDeltaMerge.merge(baseline, secondDesired, afterFirst);
        assertValue(merged, "shared", "keep");
        assertValue(merged, "servers.lobby", "ready");
        assertValue(merged, "servers.survival", "sleeping");

        Map<String, Object> remover = new LinkedHashMap<>(merged);
        remover.remove("shared");
        Map<String, Object> thirdWriter = new LinkedHashMap<>(merged);
        thirdWriter.put("servers.creative", "ready");
        Map<String, Object> afterRemoval = BackendYamlDeltaMerge.merge(merged, remover, merged);
        afterRemoval = BackendYamlDeltaMerge.merge(merged, thirdWriter, afterRemoval);
        if (afterRemoval.containsKey("shared")) {
            throw new IllegalStateException("Removed value returned during merge.");
        }
        assertValue(afterRemoval, "servers.creative", "ready");
        System.out.println("Backend YAML delta merge harness OK");
    }

    private static Map<String, Object> map(Object... entries) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) {
            values.put((String)entries[index], entries[index + 1]);
        }
        return values;
    }

    private static void assertValue(Map<String, Object> values, String path, String expected) {
        Object actual = values.get(path);
        if (!expected.equals(actual)) {
            throw new IllegalStateException(path + " expected " + expected + " but was " + actual);
        }
    }
}
