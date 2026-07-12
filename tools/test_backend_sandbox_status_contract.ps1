param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxStatusService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'private final Map<UUID, Component> statuses',
    'boolean hasState(UUID uuid)',
    'boolean contains(UUID uuid)',
    'void set(UUID uuid, Component component)',
    'void remove(UUID uuid)',
    'List<StatusEntry> statusEntries()',
    'status != null && this.hasState(uuid)',
    'new StatusEntry(uuid, Component.empty())'
)) { if (-not $service.Contains($required)) { throw "Sandbox guidance status contract missing: $required" } }
foreach ($required in @(
    'this.sandboxStatusService.statusEntries()',
    'this.publishActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX, entry.component())',
    'this.notifyActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX_NOTIFICATION'
)) { if (-not $plugin.Contains($required)) { throw "Sandbox status wiring missing: $required" } }
if ($service.Contains('Notification')) { throw 'Sandbox status service owns notification state again.' }
Write-Host "Backend Sandbox status contract OK"
