package dev.lemonos;

final class BackendAdminAtmosphereClickService {
    AdminAtmosphereClick action(int clickedSlot, int backSlot, BackendAdminWorldNavigationService<?> worldNavigationService) {
        if (clickedSlot == backSlot) {
            return new AdminAtmosphereClick(AdminAtmosphereAction.BACK, null, null);
        }
        String time = worldNavigationService.timeForSlot(clickedSlot);
        if (time != null) {
            return new AdminAtmosphereClick(AdminAtmosphereAction.TIME, time, null);
        }
        Boolean weather = worldNavigationService.weatherForSlot(clickedSlot);
        if (weather != null) {
            return new AdminAtmosphereClick(AdminAtmosphereAction.WEATHER, null, weather);
        }
        return new AdminAtmosphereClick(AdminAtmosphereAction.NONE, null, null);
    }

    record AdminAtmosphereClick(AdminAtmosphereAction action, String time, Boolean weather) {
    }

    enum AdminAtmosphereAction {
        NONE,
        BACK,
        TIME,
        WEATHER;
    }
}
