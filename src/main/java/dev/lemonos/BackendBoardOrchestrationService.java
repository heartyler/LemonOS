package dev.lemonos;

import java.util.ArrayList;
import java.util.List;

final class BackendBoardOrchestrationService {
    record Plan(boolean active, long periodTicks, boolean clearStayedClose, List<String> disabledRolePrefixes) {
        Plan {
            disabledRolePrefixes = List.copyOf(disabledRolePrefixes);
        }
    }

    Plan plan(String server, BackendBoardConfig config, List<BackendBoardDefinition> definitions) {
        boolean lobby = "lobby".equals(server);
        boolean stayedCloseEnabled = lobby && config.enabled(BackendBoardConfig.STAYED_CLOSE);
        int refreshMinutes = stayedCloseEnabled ? config.refreshMinutes(BackendBoardConfig.STAYED_CLOSE) : 1440;
        boolean active = stayedCloseEnabled;
        ArrayList<String> disabled = new ArrayList<>();

        for (BackendBoardDefinition definition : definitions) {
            if (!definition.enabledOn(server)) continue;
            if (config.enabled(definition.dataKey())) {
                active = true;
                refreshMinutes = Math.min(refreshMinutes, config.refreshMinutes(definition.dataKey()));
            } else {
                disabled.add(definition.rolePrefix());
            }
        }
        long periodTicks = active ? 1200L * Math.max(1, refreshMinutes) : 0L;
        return new Plan(active, periodTicks, lobby && !stayedCloseEnabled, disabled);
    }
}
