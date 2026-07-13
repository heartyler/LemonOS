param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPlaceAvailabilityService.java"
$runtimePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPlaceRuntimeService.java"
$lifecyclePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPlaceRuntimeLifecycleService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendPlaceAvailabilityService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$runtime = Get-Content -Raw -LiteralPath $runtimePath
$lifecycle = Get-Content -Raw -LiteralPath $lifecyclePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendPlaceAvailabilityService<S>",
    "private static final int CONNECT_TIMEOUT_MILLIS = 300",
    "private final BackendPlaceRuntimeStatusService placeRuntimeStatusService",
    "BackendPlaceAvailabilityService(BackendPlaceRuntimeStatusService placeRuntimeStatusService)",
    "boolean ready(FileConfiguration places, S server, Function<S, String> proxyName)",
    "return !this.closed(places, server, proxyName)",
    "boolean closed(FileConfiguration places, S server, Function<S, String> proxyName)",
    "return this.placeRuntimeStatusService.closed(places, proxyName.apply(server))",
    "boolean wakeable(FileConfiguration places, S server, Function<S, String> proxyName)",
    "return this.placeRuntimeStatusService.wakeable(places, proxyName.apply(server))",
    "boolean canConnect(int port)",
    "socket.connect(new InetSocketAddress(`"127.0.0.1`", port), CONNECT_TIMEOUT_MILLIS)",
    "catch (IOException | RuntimeException exception)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendPlaceAvailabilityService missing required availability contract text: $required"
    }
}

foreach ($required in @(
    "final class BackendPlaceRuntimeService<S>",
    "private final Map<S, Integer> ports",
    "private final Map<S, Boolean> availability",
    "S configure(",
    "this.ports.put(server, resolution.ports().getOrDefault(",
    "void refresh(Iterable<S> servers, S currentServer)",
    "Objects.equals(server, currentServer) || this.canConnect(server)",
    "boolean available(S server, S currentServer)",
    "boolean canConnect(S server)",
    "Map<S, Integer> ports()",
    "Map<S, Boolean> availability()"
)) {
    if (-not $runtime.Contains($required)) { throw "BackendPlaceRuntimeService missing required runtime ownership: $required" }
}

foreach ($required in @(
    "final class BackendPlaceRuntimeLifecycleService",
    "runTaskTimerAsynchronously",
    "ERROR_LOG_INTERVAL_NANOS",
    "this.errorHandler.accept(exception)",
    "this.task.cancel()"
)) {
    if (-not $lifecycle.Contains($required)) { throw "BackendPlaceRuntimeLifecycleService missing lifecycle ownership: $required" }
}

foreach ($required in @(
    "private BackendPlaceAvailabilityService<ServerId> placeAvailabilityService",
    "private BackendPlaceRuntimeService<ServerId> placeRuntimeService",
    "private BackendPlaceRuntimeLifecycleService placeRuntimeLifecycleService",
    "this.placeAvailabilityService = new BackendPlaceAvailabilityService<ServerId>(this.placeRuntimeStatusService)",
    "this.placeRuntimeService = new BackendPlaceRuntimeService<ServerId>(this.placeAvailabilityService::canConnect)",
    "this.placeRuntimeService.refresh(List.of(ServerId.values()), this.currentServer)",
    "this.placeRuntimeLifecycleService.start(100L, 100L, this::refreshAvailability)",
    "this.placeRuntimeLifecycleService.stop()",
    "return this.placeRuntimeService.available(serverId, this.currentServer)",
    "return this.placeRuntimeService.configure(",
    "return this.placeAvailabilityService.ready(this.places, serverId, target -> target.proxyName)",
    "return this.placeAvailabilityService.wakeable(this.places, serverId, target -> target.proxyName)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendPlaceAvailabilityService: $required"
    }
}

foreach ($forbidden in @(
    "private final Map<ServerId, Boolean> serverAvailability",
    "private final Map<ServerId, Integer> serverPorts",
    "private BukkitTask availabilityTask",
    "runTaskTimerAsynchronously((Plugin)this, this::refreshAvailability",
    "this.placeAvailabilityService.initialize(",
    "this.placeAvailabilityService.refresh(",
    "this.placeAvailabilityService.available(",
    "availability.put(server, this.ready(places, server, proxyName) && this.canConnect(port.applyAsInt(server)))",
    "socket.connect(new InetSocketAddress(`"127.0.0.1`", n), 300)",
    "this.serverAvailability.getOrDefault((Object)serverId, serverId == this.currentServer)",
    "this.serverAvailability.put(serverId, this.isServerReady(serverId) && this.canConnect(serverId.port))",
    "return ServerId.LOBBY;",
    "return !this.isPlaceClosed(serverId)",
    "return this.placeRuntimeStatusService.closed(this.places, serverId.proxyName)",
    "return this.placeRuntimeStatusService.wakeable(this.places, serverId.proxyName)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend place availability orchestration detail: $forbidden"
    }
}

Write-Host "LemonOS backend place availability contract tests passed."
