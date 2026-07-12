/*
 * Decompiled with CFR 0.152.
 */
package dev.lemonos.common;

public enum TextKey {
    DONE("results.done", "done."),
    SAVED("results.saved", "saved."),
    SENT("results.sent", "sent."),
    YOURE_IN("results.youre-in", "you're in."),
    ON_THE_WAY("results.on-the-way", "on the way."),
    YOURE_HERE("results.youre-here", "you're here."),
    NOTHING_CHANGED("results.nothing-changed", "nothing changed."),
    TOO_LATE("results.too-late", "too late."),
    TRY_AGAIN("results.try-again", "try again."),
    TOO_SHORT("results.too-short", "too short."),
    TOO_LONG("results.too-long", "too long."),
    TOO_LARGE("results.too-large", "too large."),
    WAITING("results.waiting", "waiting."),
    VERIFYING("results.verifying", "verifying."),
    NOT_AVAILABLE_HERE("results.not-available-here", "out of range."),
    UNAVAILABLE("results.unavailable", "no signal."),
    NOT_READY_YET("results.not-ready-yet", "not ready yet.");

    private final String path;
    private final String fallback;

    private TextKey(String string2, String string3) {
        this.path = string2;
        this.fallback = string3;
    }

    public String path() {
        return this.path;
    }

    public String fallback() {
        return this.fallback;
    }
}
