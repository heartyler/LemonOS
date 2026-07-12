/*
 * Backend-side LemonOS admin protocol constants.
 */
package dev.lemonos;

final class BackendAdminProtocol {
    static final String ADMIN_CHANNEL = "lemonos:admin2";
    static final byte ACCESS_REQUEST_MAGIC = 42;
    static final byte ACCESS_ACK_MAGIC = 43;
    static final String SKIN_APPLY = "skin-apply";
    static final String SKIN_RESULT = "skin-result";
    static final String OPEN_CUBEE = "open-cubee";
    static final String WAKE_PLACE = "wake-place";
    static final String SEND_PLAYER_PLACE = "send-player-place";
    static final String SEND_PLAYER_RESULT = "send-player-result";
    static final String SEND_RESULT_SENT = "sent";
    static final String SEND_RESULT_TRY_AGAIN = "try-again";
    static final String RESULT_SAVED = "saved";
    static final String RESULT_TRY_AGAIN = "try-again";

    private BackendAdminProtocol() {
    }
}
