package dev.lemonos;

final class BackendSandboxInputModelService {
    Range sizeRange() {
        return new Range(1, 16);
    }

    String sizeGuidance() {
        return "Type 1-16.";
    }

    String failureMessage(int value, Range range) {
        return value > range.max() ? "too large." : "try again.";
    }

    boolean inRange(int value, Range range) {
        return value >= range.min() && value <= range.max();
    }

    record Range(int min, int max) {
    }
}
