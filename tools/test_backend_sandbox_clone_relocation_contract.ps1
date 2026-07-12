param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$previewService = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxPreviewService.java")

foreach ($required in @(
    'private static final int CLONE_RELOCATIONS = 2;',
    'if (this.handleClonePreviewRelocation(playerInteractEvent))',
    'Object preview = this.sandboxPreviewService.clonePreview(player.getUniqueId());',
    'itemStack.getType() != this.basicToolMaterial()',
    'if (!currentPreview.world.equals(target.getWorld()))',
    'if (minX == currentPreview.minX && minY == currentPreview.minY && minZ == currentPreview.minZ)',
    'if (currentPreview.movesLeft <= 0)',
    'if (minY < currentPreview.world.getMinHeight() || maxY >= currentPreview.world.getMaxHeight())',
    'currentPreview.movesLeft - 1',
    'relocated.entities.addAll(currentPreview.entities);',
    'this.sandboxPreviewService.setClone(player.getUniqueId(), relocated);',
    'this.touchSandboxIdleTimeout(player);',
    'this.sendSandboxCloneReadyStatus(player, relocated.movesLeft);',
    'remaining == 1 ? "1 move left" : remaining == 0 ? "no moves left" : remaining + " moves left"',
    '"open cubee. (" + moveStatus + ")"',
    'new ClonePreview(world, clonePlan.minX(), clonePlan.minY(), clonePlan.minZ(), clonePlan.maxX(), clonePlan.maxY(), clonePlan.maxZ(), CLONE_RELOCATIONS)',
    'private final int movesLeft;'
)) {
    if (-not $plugin.Contains($required)) {
        throw "Clone relocation contract missing: $required"
    }
}

$handler = [regex]::Match($plugin, 'private boolean handleClonePreviewRelocation\(PlayerInteractEvent playerInteractEvent\) \{(?s).*?\n    \}\r?\n\r?\n    private void sendSandboxCloneRelocationFailure')
if (-not $handler.Success) {
    throw "Could not isolate Clone relocation handler."
}
$setIndex = $handler.Value.IndexOf('this.sandboxPreviewService.setClone(player.getUniqueId(), relocated);')
foreach ($guard in @(
    'if (!currentPreview.world.equals(target.getWorld()))',
    'if (minX == currentPreview.minX && minY == currentPreview.minY && minZ == currentPreview.minZ)',
    'if (currentPreview.movesLeft <= 0)',
    'if (minY < currentPreview.world.getMinHeight() || maxY >= currentPreview.world.getMaxHeight())'
)) {
    $guardIndex = $handler.Value.IndexOf($guard)
    if ($guardIndex -lt 0 -or $guardIndex -gt $setIndex) {
        throw "Clone relocation guard must run before preview replacement: $guard"
    }
}
if (-not $previewService.Contains('Object clonePreview(UUID uuid)') -or
    -not $previewService.Contains('return uuid == null ? null : this.clonePreviews.get(uuid);')) {
    throw "Clone preview lookup boundary is missing."
}

foreach ($required in @(
    'new ButtonSpec(13, Material.WHITE_SHULKER_BOX, "Clone", null)',
    'new ButtonSpec(13, Material.TINTED_GLASS, "Clear", null)',
    'new ButtonSpec(13, Material.LIME_GLAZED_TERRACOTTA, "Flip", null)',
    'new ButtonSpec(13, Material.MAGENTA_GLAZED_TERRACOTTA, "Rotate", null)'
)) {
    if (-not $plugin.Contains($required)) {
        throw "Sandbox Confirm Slot 13 identity mismatch: $required"
    }
}
foreach ($forbidden in @(
    'new ButtonSpec(13, Material.SHULKER_SHELL, "Clone", null)',
    'new ButtonSpec(13, Material.OXIDIZED_COPPER_BULB, "Flip", null)'
)) {
    if ($plugin.Contains($forbidden)) {
        throw "Old Sandbox Confirm item remains: $forbidden"
    }
}

Write-Host "Backend Sandbox Clone relocation contract OK"
