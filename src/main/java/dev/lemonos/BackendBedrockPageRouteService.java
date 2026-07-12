package dev.lemonos;

final class BackendBedrockPageRouteService {
    PageRoute route(boolean bedrockPlayer) {
        return bedrockPlayer ? PageRoute.BEDROCK : PageRoute.JAVA;
    }

    enum PageRoute {
        BEDROCK,
        JAVA;

        boolean bedrock() {
            return this == BEDROCK;
        }
    }
}
