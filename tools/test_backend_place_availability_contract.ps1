param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPlaceAvailabilityService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendPlaceAvailabilityService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$detectServer = [regex]::Match($backend, 'private ServerId detectServer\(\) \{(?s).*?\n    \}')
if (-not $detectServer.Success) { throw "Could not isolate backend server detection." }
if ([regex]::Matches($detectServer.Value, 'for \(ServerId serverId : ServerId\.values\(\)\)').Count -ne 2) {
    throw "Backend server detection must populate every port before selecting the current server."
}
if ($detectServer.Value.IndexOf('this.serverPorts.put(') -gt $detectServer.Value.IndexOf('if (serverId.proxyName.equals(resolution.currentServer()))')) {
    throw "Backend server detection selects the current server before populating its port map."
}

foreach ($required in @(
    "final class BackendPlaceAvailabilityService<S>",
    "private static final int CONNECT_TIMEOUT_MILLIS = 300",
    "private final BackendPlaceRuntimeStatusService placeRuntimeStatusService",
    "BackendPlaceAvailabilityService(BackendPlaceRuntimeStatusService placeRuntimeStatusService)",
    "void initialize(Map<S, Boolean> availability, Iterable<S> servers, S currentServer)",
    "availability.put(server, Objects.equals(server, currentServer))",
    "void refresh(Map<S, Boolean> availability, Iterable<S> servers, S currentServer, ToIntFunction<S> port)",
    "availability.put(server, true)",
    "availability.put(server, this.canConnect(port.applyAsInt(server)))",
    "boolean available(Map<S, Boolean> availability, S server, S currentServer)",
    "return availability.getOrDefault((Object)server, Objects.equals(server, currentServer))",
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
    "private final Map<ServerId, Boolean> serverAvailability = new ConcurrentHashMap<ServerId, Boolean>()",
    "private BackendPlaceAvailabilityService<ServerId> placeAvailabilityService",
    "this.placeAvailabilityService = new BackendPlaceAvailabilityService<ServerId>(this.placeRuntimeStatusService)",
    "this.placeAvailabilityService.initialize(this.serverAvailability, List.of(ServerId.values()), this.currentServer)",
    "this.placeAvailabilityService.refresh(this.serverAvailability, List.of(ServerId.values()), this.currentServer, this::serverPort)",
    "return this.serverPorts.getOrDefault(serverId, serverId.defaultPort)",
    "return this.placeAvailabilityService.canConnect(n)",
    "return this.placeAvailabilityService.available(this.serverAvailability, serverId, this.currentServer)",
    "return this.placeAvailabilityService.ready(this.places, serverId, target -> target.proxyName)",
    "return this.placeAvailabilityService.wakeable(this.places, serverId, target -> target.proxyName)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendPlaceAvailabilityService: $required"
    }
}

foreach ($forbidden in @(
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
