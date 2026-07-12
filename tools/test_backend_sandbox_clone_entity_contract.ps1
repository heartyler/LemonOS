param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$plan = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxClonePreviewPlanService.java")

foreach ($required in @(
    'Predicate<Entity> excludedEntity',
    'for (Entity entity : world.getEntities())',
    'excludedEntity.test(entity)',
    'EntitySnapshot snapshot = entity.createSnapshot();',
    'source.getX() - bounds.minX()',
    'source.getY() - bounds.minY()',
    'source.getZ() - bounds.minZ()',
    'location.getX() >= bounds.minX() && location.getX() < bounds.maxX() + 1.0',
    'record SourceEntity(UUID sourceId, UUID vehicleSourceId',
    'List<SourceEntity> entities'
)) {
    if (-not $plan.Contains($required)) {
        throw "Clone entity snapshot contract missing: $required"
    }
}

foreach ($required in @(
    'clonePlan = this.sandboxClonePreviewPlanService.build(world, bounds, location, this::isSandboxCloneExcludedEntity);',
    'entity == null || entity instanceof Player',
    '"lemonos".equalsIgnoreCase(key.getNamespace())',
    'relocated.entities.addAll(currentPreview.entities);',
    'drawingChange.entities.add(new EntityChange',
    'entityChange.snapshot.createEntity(entityChange.target.clone())',
    'vehicle.addPassenger(passenger)',
    'private boolean removeClonedEntities(DrawingChange drawingChange)',
    'this.applyChange(drawingChange, true);',
    'this.spawnClonedEntities(drawingChange)',
    'return this.blocks.isEmpty() && this.entities.isEmpty();',
    'private final List<CloneEntity> entities = new ArrayList<CloneEntity>();',
    'private final List<EntityChange> entities = new ArrayList<EntityChange>();'
)) {
    if (-not $plugin.Contains($required)) {
        throw "Clone entity lifecycle contract missing: $required"
    }
}

$previewMethod = [regex]::Match($plugin, 'private void createClonePreview\(Player player, DrawingState drawingState, Location location\) \{(?s).*?\n    \}\r?\n\r?\n    private void createClearPreview')
if (-not $previewMethod.Success) {
    throw "Could not isolate entity-aware Clone preview creation."
}
if ($previewMethod.Value.Contains('.createEntity(')) {
    throw "Clone preview must not create ghost entities."
}

$placementMethod = [regex]::Match($plugin, 'private boolean spawnClonedEntities\(DrawingChange drawingChange\) \{(?s).*?\n    \}\r?\n\r?\n    private boolean removeClonedEntities')
if (-not $placementMethod.Success -or
    $placementMethod.Value.IndexOf('entityChange.snapshot.createEntity') -gt $placementMethod.Value.IndexOf('vehicle.addPassenger(passenger)')) {
    throw "Clone entities must be created before passenger relationships are restored."
}

Write-Host "Backend Sandbox Clone entity contract OK"
