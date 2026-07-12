package dev.lemonos;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** Concurrent registry with token-aware compare-and-remove semantics. */
final class BackendOperationRegistry<K, O> {
    record Entry<O>(BackendOperationToken token, O operation) {
        Entry {
            Objects.requireNonNull(token, "token");
            Objects.requireNonNull(operation, "operation");
        }
    }

    private final ConcurrentHashMap<K, Entry<O>> entries = new ConcurrentHashMap<>();

    boolean beginIfAbsent(K key, BackendOperationToken token, O operation) {
        return this.entries.putIfAbsent(this.key(key), new Entry<>(token, operation)) == null;
    }

    Optional<Entry<O>> replace(K key, BackendOperationToken token, O operation) {
        return Optional.ofNullable(this.entries.put(this.key(key), new Entry<>(token, operation)));
    }

    Optional<Entry<O>> current(K key) {
        return Optional.ofNullable(this.entries.get(this.key(key)));
    }

    boolean isCurrent(K key, BackendOperationToken token) {
        Entry<O> entry = this.entries.get(this.key(key));
        return entry != null && entry.token().sameOperation(token);
    }

    boolean useIfCurrent(K key, BackendOperationToken token, Consumer<O> action) {
        Objects.requireNonNull(action, "action");
        AtomicBoolean used = new AtomicBoolean(false);
        this.entries.computeIfPresent(this.key(key), (ignored, entry) -> {
            if (entry.token().sameOperation(token)) {
                action.accept(entry.operation());
                used.set(true);
            }
            return entry;
        });
        return used.get();
    }

    Optional<Entry<O>> removeIfCurrent(K key, BackendOperationToken token) {
        K safeKey = this.key(key);
        Entry<O> entry = this.entries.get(safeKey);
        if (entry == null || !entry.token().sameOperation(token) || !this.entries.remove(safeKey, entry)) {
            return Optional.empty();
        }
        return Optional.of(entry);
    }

    Optional<Entry<O>> remove(K key) {
        return Optional.ofNullable(this.entries.remove(this.key(key)));
    }

    List<Entry<O>> snapshot() {
        return List.copyOf(this.entries.values());
    }

    void clear(Consumer<O> cleanup) {
        Objects.requireNonNull(cleanup, "cleanup");
        for (K key : List.copyOf(this.entries.keySet())) {
            Entry<O> entry = this.entries.remove(key);
            if (entry != null) cleanup.accept(entry.operation());
        }
    }

    int size() {
        return this.entries.size();
    }

    private K key(K key) {
        return Objects.requireNonNull(key, "key");
    }
}
