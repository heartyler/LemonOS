package dev.lemonos;

public final class BackendCubeeRoutingHarness {
    public static void main(String[] args) {
        BackendCubeeRoutingService service = new BackendCubeeRoutingService();
        require(service.homeAction(2, 2, 6, true, false, false, false, false, false)
                == BackendCubeeRoutingService.HomeAction.LOOK, "look route failed");
        require(service.homeAction(6, 2, 6, false, true, false, false, false, false)
                == BackendCubeeRoutingService.HomeAction.CARE, "care route failed");
        require(service.homeAction(12, 2, 6, false, false, false, false, true, false)
                == BackendCubeeRoutingService.HomeAction.PEOPLE, "people route failed");
        require(service.homeAction(14, 2, 6, false, false, false, true, true, true)
                == BackendCubeeRoutingService.HomeAction.SURVIVAL_HOME, "survival precedence changed");
        require(service.homeAction(14, 2, 6, false, false, false, false, false, true)
                == BackendCubeeRoutingService.HomeAction.SANDBOX, "sandbox route failed");
        require(service.homeAction(99, 2, 6, true, true, false, false, true, true)
                == BackendCubeeRoutingService.HomeAction.NONE, "unknown home slot routed");
        require(service.isNavBack(8, 8) && !service.isNavBack(7, 8), "back route failed");

        require(service.defaultLaunchPlan(true, false, false, false, false, false).action()
                == BackendCubeeRoutingService.LaunchAction.OPEN_GO, "lobby launch failed");
        require(service.defaultLaunchPlan(false, true, false, true, false, false).action()
                == BackendCubeeRoutingService.LaunchAction.OPEN_LAST_PEOPLE, "survival launch failed");
        require(service.defaultLaunchPlan(false, false, true, false, true, false).action()
                == BackendCubeeRoutingService.LaunchAction.OPEN_DRAWING, "creative launch failed");
        require(service.defaultLaunchPlan(false, false, false, false, false, true).action()
                == BackendCubeeRoutingService.LaunchAction.OPEN_BEDROCK_HOME, "Bedrock fallback failed");

        require(service.rememberedSurfacePlan(CubeeSurface.PLACES, CubeeRoot.CUBEE, false, false, false, false, false).action()
                == BackendCubeeRoutingService.LaunchAction.OPEN_GO, "remembered places failed");
        require(service.rememberedSurfacePlan(CubeeSurface.ADMIN_PEOPLE, CubeeRoot.CARE, false, false, true, false, false).action()
                == BackendCubeeRoutingService.LaunchAction.OPEN_LAST_ADMIN_PEOPLE, "remembered admin failed");
        BackendCubeeRoutingService.LaunchPlan deniedAdmin = service.rememberedSurfacePlan(
                CubeeSurface.ADMIN_PEOPLE, CubeeRoot.CARE, false, false, false, false, false);
        require(deniedAdmin.action() == BackendCubeeRoutingService.LaunchAction.OPEN_JAVA_HOME
                && deniedAdmin.root() == CubeeRoot.CUBEE && deniedAdmin.surface() == CubeeSurface.HOME,
                "denied admin fallback failed");
        System.out.println("Backend Cubee routing harness OK");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
