package dev.lemonos;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.bukkit.entity.Player;

final class BackendAdminAccessActionService {
    private final Supplier<List<String>> accessHolderNames;
    private final Supplier<List<String>> candidateNames;
    private final Function<String, String> normalizeAccessName;
    private final Predicate<String> safeAdminName;

    BackendAdminAccessActionService(Supplier<List<String>> accessHolderNames, Supplier<List<String>> candidateNames, Function<String, String> normalizeAccessName, Predicate<String> safeAdminName) {
        this.accessHolderNames = accessHolderNames;
        this.candidateNames = candidateNames;
        this.normalizeAccessName = normalizeAccessName;
        this.safeAdminName = safeAdminName;
    }

    String normalize(String name) {
        return this.normalizeAccessName.apply(name);
    }

    boolean isHolder(String name) {
        return this.accessHolderNames.get().contains(this.normalize(name));
    }

    boolean canGive(String name) {
        String normalized = this.normalize(name);
        return this.safeAdminName.test(normalized) && !this.isHolder(normalized) && this.candidateNames.get().contains(normalized);
    }

    boolean isSelfHolder(Player player, String name) {
        return this.normalizeAccessName.apply(player.getName()).equals(this.normalize(name));
    }
}
