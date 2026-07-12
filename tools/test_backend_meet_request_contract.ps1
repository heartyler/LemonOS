param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendMeetRequestService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendMeetRequestService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendMeetRequestService<K>",
    "private static final long REQUEST_TIMEOUT_TICKS = 400L",
    "private static final long LATE_REQUEST_TICKS = 200L",
    "private final Map<UUID, RequestState<K>> incomingRequests",
    "private final Map<UUID, RequestState<K>> outgoingRequests",
    "private final Map<UUID, BukkitTask> lateRequestTasks",
    "BackendMeetRequestService(Plugin plugin, Predicate<UUID> busy, Predicate<Player> sociallyBusy, Predicate<K> visitKind, BiConsumer<Player, Player> visitTravel, BiConsumer<Player, Player> inviteTravel, Consumer<Player> notificationSound, Consumer<Player> pendingStatus)",
    "RequestState<K> incoming(UUID uuid)",
    "boolean hasActive(UUID uuid)",
    "void create(Player sender, Player receiver, K kind, String receiverMessage)",
    "this.busy.test(sender.getUniqueId()) || this.sociallyBusy.test(receiver)",
    "requestState.timeoutTask = Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.expire(requestState), REQUEST_TIMEOUT_TICKS)",
    "this.pendingStatus.accept(sender)",
    "receiver.sendMessage(((TextComponent)Component.text((String)sender.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space())).append((Component)Component.text((String)receiverMessage, (TextColor)NamedTextColor.GRAY)))",
    "this.notificationSound.accept(receiver)",
    "receiver.sendMessage((Component)Component.text((String)`"open cubee.`", (TextColor)NamedTextColor.GRAY))",
    "void accept(Player receiver, RequestState<K> requestState)",
    "receiver.sendMessage((Component)Component.text((String)`"nothing changed.`", (TextColor)NamedTextColor.DARK_GRAY))",
    "this.visitTravel.accept(sender, receiver)",
    "this.inviteTravel.accept(receiver, sender)",
    "void decline(RequestState<K> requestState)",
    "void expire(RequestState<K> requestState)",
    "requestState.expired = true",
    "this.markLate(requestState.receiver)",
    "void clearFor(UUID uuid)",
    "boolean consumeLate(UUID uuid)",
    "boolean matches(UUID sender, K kind, RequestState<K> requestState)",
    "void cancelAllAndClear()",
    "static final class RequestState<K>"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendMeetRequestService missing required meet request contract text: $required"
    }
}

foreach ($required in @(
    "private BackendMeetRequestService<RequestKind> meetRequestService",
    "this.meetRequestService = new BackendMeetRequestService<RequestKind>((Plugin)this, this::isBusy, this::isSociallyBusy, kind -> kind == RequestKind.VISIT, this::startLocalTravel, this::startLocalTravel, player -> this.playHomeSound(player, `"notification`"), this::sendWaitingStatus)",
    "this.meetRequestService.cancelAllAndClear()",
    "this.meetRequestService.incoming(player.getUniqueId()) != null",
    "this.meetRequestService.consumeLate(player.getUniqueId())",
    "BackendMeetRequestService.RequestState<RequestKind> requestState = this.meetRequestService.incoming(player.getUniqueId())",
    "this.meetRequestService.create(player, player2, requestKind, requestKind == RequestKind.VISIT ? `"wants to visit you.`" : `"invites you.`")",
    "this.meetRequestService.accept(player, requestState)",
    "this.meetRequestService.decline(requestState)",
    "this.meetRequestService.clearFor(uUID)",
    "this.meetRequestService.matches(cubeeHolder.requestSender, cubeeHolder.requestKind, requestState)",
    "this.meetRequestService.hasActive(uUID)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendMeetRequestService: $required"
    }
}

foreach ($forbidden in @(
    "private final Map<UUID, RequestState> incomingRequests",
    "private final Map<UUID, RequestState> outgoingRequests",
    "private final Map<UUID, BukkitTask> lateRequestTasks",
    "private static final class RequestState",
    "new RequestState(player.getUniqueId(), player2.getUniqueId(), requestKind)",
    "this.incomingRequests.put",
    "this.outgoingRequests.put",
    "this.lateRequestTasks.put",
    "private void expireRequest(",
    "private void registerRequest(",
    "private void unlinkRequest(",
    "private boolean consumeLateRequest("
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend meet request lifecycle detail: $forbidden"
    }
}

Write-Host "LemonOS backend meet request contract tests passed."
