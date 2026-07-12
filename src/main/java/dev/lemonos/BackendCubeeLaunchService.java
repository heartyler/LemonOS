package dev.lemonos;

final class BackendCubeeLaunchService {
    LaunchPlan rememberedSurfacePlan(
            CubeeSurface surface,
            CubeeRoot root,
            boolean currentServerLobby,
            boolean peopleShortcutPublic,
            boolean admin,
            boolean sandboxAvailable,
            boolean bedrock) {
        CubeeRoot plannedRoot = root;
        CubeeSurface plannedSurface = surface;
        if (surface == CubeeSurface.PLACES) {
            return new LaunchPlan(LaunchAction.OPEN_GO, null, null);
        }
        if (surface == CubeeSurface.PEOPLE) {
            if (!currentServerLobby && peopleShortcutPublic) {
                return new LaunchPlan(LaunchAction.OPEN_LAST_PEOPLE, null, null);
            }
            plannedSurface = CubeeSurface.HOME;
        } else if (surface == CubeeSurface.ADMIN_PEOPLE) {
            if (admin) {
                return new LaunchPlan(LaunchAction.OPEN_LAST_ADMIN_PEOPLE, CubeeRoot.CARE, null);
            }
            plannedRoot = CubeeRoot.CUBEE;
            plannedSurface = CubeeSurface.HOME;
        } else if (surface == CubeeSurface.SANDBOX) {
            if (sandboxAvailable) {
                return new LaunchPlan(LaunchAction.OPEN_DRAWING, null, null);
            }
            plannedSurface = CubeeSurface.HOME;
        }
        if (plannedRoot == CubeeRoot.CARE) {
            if (admin) {
                return new LaunchPlan(LaunchAction.OPEN_ADMIN, null, plannedSurface);
            }
            plannedRoot = CubeeRoot.CUBEE;
        }
        return new LaunchPlan(bedrock ? LaunchAction.OPEN_BEDROCK_HOME : LaunchAction.OPEN_JAVA_HOME, plannedRoot, plannedSurface);
    }

    LaunchPlan defaultLaunchPlan(
            boolean currentServerLobby,
            boolean currentServerSurvival,
            boolean currentServerCreative,
            boolean peopleShortcutPublic,
            boolean sandboxAvailable,
            boolean bedrock) {
        if (currentServerLobby) {
            return new LaunchPlan(LaunchAction.OPEN_GO, CubeeRoot.CUBEE, null);
        }
        if (currentServerSurvival && peopleShortcutPublic) {
            return new LaunchPlan(LaunchAction.OPEN_LAST_PEOPLE, CubeeRoot.CUBEE, null);
        }
        if (currentServerCreative && sandboxAvailable) {
            return new LaunchPlan(LaunchAction.OPEN_DRAWING, CubeeRoot.CUBEE, null);
        }
        return new LaunchPlan(bedrock ? LaunchAction.OPEN_BEDROCK_HOME : LaunchAction.OPEN_JAVA_HOME, CubeeRoot.CUBEE, CubeeSurface.HOME);
    }

    record LaunchPlan(LaunchAction action, CubeeRoot root, CubeeSurface surface) {
    }

    enum LaunchAction {
        OPEN_GO,
        OPEN_LAST_PEOPLE,
        OPEN_LAST_ADMIN_PEOPLE,
        OPEN_DRAWING,
        OPEN_ADMIN,
        OPEN_BEDROCK_HOME,
        OPEN_JAVA_HOME;
    }
}
