/*
 * Backend-side LemonOS display config adapter.
 */
package dev.lemonos;

interface BackendDisplayConfig {
    boolean booleanValue(String path, boolean fallback);

    int intValue(String path, int fallback, int minimum, int maximum);

    double doubleValue(String path, double fallback, double minimum, double maximum);

    double doubleValue(String primaryPath, String legacyPath, double fallback, double minimum, double maximum);

    String stringValue(String path, String fallback);
}
