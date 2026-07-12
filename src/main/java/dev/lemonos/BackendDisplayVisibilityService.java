/*
 * Backend-side LemonOS display visibility policy.
 */
package dev.lemonos;

final class BackendDisplayVisibilityService {
    private static final String STAYED_CLOSE_TITLE_DISPLAY = "stayed_close_title";
    private static final String STAYED_CLOSE_SUBTITLE_DISPLAY = "stayed_close_subtitle";
    private static final String STAYED_CLOSE_BOTTOM_DISPLAY = "stayed_close_bottom";
    private static final String STAYED_CLOSE_NAME_DISPLAY = "stayed_close_name_";
    private static final String STAYED_CLOSE_TIME_DISPLAY = "stayed_close_time_";
    private static final String STAYED_CLOSE_BEDROCK_TITLE_DISPLAY = "stayed_close_bedrock_title";
    private static final String STAYED_CLOSE_BEDROCK_SUBTITLE_DISPLAY = "stayed_close_bedrock_subtitle";
    private static final String STAYED_CLOSE_BEDROCK_BOTTOM_DISPLAY = "stayed_close_bedrock_bottom";
    private static final String STAYED_CLOSE_BEDROCK_ROW_DISPLAY = "stayed_close_bedrock_row_";

    boolean hudVisible(String role, boolean bedrockEnabled, boolean bedrockPlayer) {
        boolean bedrockRole = role.contains("bedrock_");
        if (bedrockPlayer && !bedrockEnabled || bedrockRole && !bedrockPlayer || !bedrockRole && bedrockPlayer) {
            return false;
        }
        return true;
    }

    boolean stayedCloseVisible(String role, boolean bedrockEnabled, boolean bedrockPlayer) {
        boolean bedrockRole = this.isStayedCloseBedrockRole(role);
        boolean javaPreferredRole = role != null && (role.startsWith(STAYED_CLOSE_NAME_DISPLAY) || role.startsWith(STAYED_CLOSE_TIME_DISPLAY) || bedrockEnabled && (STAYED_CLOSE_TITLE_DISPLAY.equals(role) || STAYED_CLOSE_SUBTITLE_DISPLAY.equals(role) || STAYED_CLOSE_BOTTOM_DISPLAY.equals(role)));
        if (bedrockPlayer && !bedrockEnabled) {
            return false;
        }
        if (bedrockRole && !bedrockEnabled) {
            return false;
        }
        if (bedrockRole && !bedrockPlayer || javaPreferredRole && bedrockPlayer) {
            return false;
        }
        return true;
    }

    boolean isStayedCloseBedrockRole(String role) {
        return STAYED_CLOSE_BEDROCK_TITLE_DISPLAY.equals(role) || STAYED_CLOSE_BEDROCK_SUBTITLE_DISPLAY.equals(role) || STAYED_CLOSE_BEDROCK_BOTTOM_DISPLAY.equals(role) || role != null && role.startsWith(STAYED_CLOSE_BEDROCK_ROW_DISPLAY);
    }
}
