param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendCubeeItemService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendCubeeItemService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendCubeeItemService",
    "private final Predicate<ItemStack> cubeeItem;",
    "BackendCubeeItemService(Predicate<ItemStack> cubeeItem)",
    "InventoryMode ensureMode(boolean selectSystemSlot)",
    "return new InventoryMode(false, true, selectSystemSlot, false);",
    "InventoryMode normalizeMode()",
    "return new InventoryMode(true, false, false, true);",
    "AccessAction accessAction(boolean authLocked, boolean enabled, boolean visible)",
    "return AccessAction.HIDE_AUTH;",
    "return AccessAction.PURGE;",
    "return AccessAction.CONTINUE;",
    "PlacementAction placementAction(boolean occupiedByOtherItem, boolean lobby, boolean moved)",
    "return PlacementAction.PLACE;",
    "return PlacementAction.HIDE;",
    "boolean hasOutsideSystemSlot(PlayerInventory inventory, int systemSlot)",
    "slot == systemSlot",
    "return this.cubeeItem.test(inventory.getItemInOffHand());",
    "void purge(PlayerInventory inventory)",
    "inventory.setItem(slot, null);",
    "inventory.setItemInOffHand(null);",
    "void purgeOutsideSystemSlot(PlayerInventory inventory, int systemSlot)",
    "boolean moveItemFromSystemSlot(PlayerInventory inventory, int systemSlot)",
    "ItemStack itemStack = inventory.getItem(systemSlot);",
    "this.isEmpty(itemStack) || this.cubeeItem.test(itemStack)",
    "inventory.setItem(slot, itemStack);",
    "inventory.setItem(systemSlot, null);",
    "private boolean isEmpty(ItemStack itemStack)",
    "return itemStack == null || itemStack.getType().isAir();",
    "enum AccessAction",
    "enum PlacementAction",
    "record InventoryMode(boolean purgeAll, boolean fastKeep, boolean selectSystemSlot, boolean updateInventory)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendCubeeItemService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendCubeeItemService cubeeItemService;",
    "new BackendCubeeItemService(this::isCubee)",
    "this.applyCubeeInventoryMode(player, this.cubeeItemService.ensureMode(bl));",
    "this.applyCubeeInventoryMode(player, this.cubeeItemService.normalizeMode());",
    "private void applyCubeeInventoryMode(Player player, BackendCubeeItemService.InventoryMode mode)",
    "boolean authLocked = this.isAuthLocked(player);",
    "boolean cubeeEnabled = !authLocked && this.cubeeEnabled();",
    "boolean cubeeVisible = cubeeEnabled && this.cubeeVisible(player);",
    "this.cubeeItemService.accessAction(authLocked, cubeeEnabled, cubeeVisible)",
    "mode.fastKeep() && this.isCubee(playerInventory.getItem(n)) && !this.hasCubeeOutsideSystemSlot(playerInventory)",
    "if (mode.purgeAll())",
    "this.cubeeItemService.purgeOutsideSystemSlot(playerInventory, n);",
    "this.cubeeItemService.purge(playerInventory);",
    "return this.cubeeItemService.hasOutsideSystemSlot(playerInventory, this.cubeeSlot());",
    "return this.cubeeItemService.moveItemFromSystemSlot(playerInventory, this.cubeeSlot());",
    "this.cubeeItemService.purge(player.getInventory());",
    "boolean moved = occupiedByOtherItem && this.currentServer == ServerId.LOBBY && this.moveItemFromCubeeSlot(playerInventory);",
    "this.cubeeItemService.placementAction(occupiedByOtherItem, this.currentServer == ServerId.LOBBY, moved)",
    "this.setCubeeVisible(player, false);",
    "playerInventory.setItem(n, this.cubeeItem());",
    "this.systemItem(Material.HONEYCOMB",
    "itemStack.getType() != Material.HONEYCOMB",
    "private void selectCubeeSystemSlot(PlayerInventory playerInventory, int systemSlot, BackendCubeeItemService.InventoryMode mode)",
    "private void finishCubeeInventoryMode(Player player, BackendCubeeItemService.InventoryMode mode)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendCubeeItemService: $snippet"
    }
}

if ($Plugin.Contains("this.systemItem(Material.BOOK") -or $Plugin.Contains("itemStack.getType() != Material.BOOK")) {
    throw "LemonOSPlugin still uses the former Book identity for Cubee."
}

if ($Plugin.Contains('SYSTEM_ITEM_HONEYPAD') -or
    $Plugin.Contains('"honeypad"') -or
    $Plugin.Contains('isLegacySystemItem(itemMeta, "Cubee"') -or
    $Plugin.Contains('isLegacySystemItem(itemMeta, "HoneyPad"')) {
    throw "Cubee item recognition must use only HONEYCOMB with the cubee marker."
}

$ensureRegion = [regex]::Match($Plugin, "private void ensureCubee\(Player player, boolean bl\) \{(?s).*?\n    \}\r?\n\r?\n    private void normalizeCubee")
if (-not $ensureRegion.Success) {
    throw "Could not isolate ensureCubee lifecycle."
}

$normalizeRegion = [regex]::Match($Plugin, "private void normalizeCubee\(Player player\) \{(?s).*?\n    \}\r?\n\r?\n    private void applyCubeeInventoryMode")
if (-not $normalizeRegion.Success) {
    throw "Could not isolate normalizeCubee lifecycle."
}

$forbiddenLifecycleSnippets = @(
    "if (this.isAuthLocked(player))",
    "if (!this.cubeeEnabled())",
    "if (!this.cubeeVisible(player))",
    "this.cubeeItemService.purgeOutsideSystemSlot",
    "this.cubeeItemService.purge(playerInventory)"
)

foreach ($snippet in $forbiddenLifecycleSnippets) {
    if ($ensureRegion.Value.Contains($snippet) -or $normalizeRegion.Value.Contains($snippet)) {
        throw "LemonOSPlugin Cubee wrapper still owns inventory normalization policy: $snippet"
    }
}

Write-Host "Backend Cubee item contract OK"
