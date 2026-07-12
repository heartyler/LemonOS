package dev.lemonos;

import org.bukkit.entity.Player;

final class BackendPluginMessageRouterService {
    private final BackendOpenCubeeMessageService openCubeeMessageService;
    private final BackendSkinProtocolService skinProtocolService;
    private final BackendAccessLegacyService accessLegacyService;
    private final BackendAdminSendProtocolService adminSendProtocolService;

    BackendPluginMessageRouterService(
            BackendOpenCubeeMessageService openCubeeMessageService,
            BackendSkinProtocolService skinProtocolService,
            BackendAccessLegacyService accessLegacyService,
            BackendAdminSendProtocolService adminSendProtocolService) {
        this.openCubeeMessageService = openCubeeMessageService;
        this.skinProtocolService = skinProtocolService;
        this.accessLegacyService = accessLegacyService;
        this.adminSendProtocolService = adminSendProtocolService;
    }

    void route(Player player, byte[] data) {
        if (this.openCubeeMessageService.handleOpenCubeeMessage(player, data)) {
            return;
        }
        if (this.skinProtocolService.handleSkinResultMessage(data)) {
            return;
        }
        if (this.adminSendProtocolService.handleResult(data)) {
            return;
        }
        this.accessLegacyService.handleAccessMessage(data);
    }
}
