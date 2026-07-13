package dev.lemonos;

import java.util.ArrayList;
import java.util.List;

final class BackendHudOrchestrationService {
    record Plan(boolean active, long periodTicks, List<String> disabledRolePrefixes) {
        Plan {
            disabledRolePrefixes = List.copyOf(disabledRolePrefixes);
        }
    }

    Plan plan(String server, BackendHudConfig config, List<BackendHudDefinition> definitions) {
        int refreshMinutes = 1440;
        boolean active = false;
        ArrayList<String> disabled = new ArrayList<>();

        for (BackendHudDefinition definition : definitions) {
            if (!definition.enabledOn(server)) continue;
            if (config.enabled(definition.dataKey())) {
                active = true;
                refreshMinutes = Math.min(refreshMinutes, config.refreshMinutes(definition.dataKey()));
            } else {
                disabled.add(definition.rolePrefix());
            }
        }
        long periodTicks = active ? 1200L * Math.max(1, refreshMinutes) : 0L;
        return new Plan(active, periodTicks, disabled);
    }
}
