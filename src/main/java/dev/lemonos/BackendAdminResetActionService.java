package dev.lemonos;

import java.util.function.Predicate;

final class BackendAdminResetActionService {
    private final Predicate<String> resetRequestExists;

    BackendAdminResetActionService(Predicate<String> resetRequestExists) {
        this.resetRequestExists = resetRequestExists;
    }

    boolean canOpen(String token) {
        return token != null && this.resetRequestExists.test(token);
    }

    boolean canResolve(String token) {
        return this.canOpen(token);
    }
}
