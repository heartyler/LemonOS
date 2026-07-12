param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$required = @(
    "import org.bukkit.event.inventory.InventoryCreativeEvent;",
    "public void onCreativeInventory(InventoryCreativeEvent inventoryCreativeEvent)",
    "this.currentServer != ServerId.CREATIVE",
    "!this.cubeeEnabled() || !this.cubeeVisible(player)",
    "Bukkit.getScheduler().runTask((Plugin)this, () -> this.repairCreativeCubeeSlot(player));",
    "private void repairCreativeCubeeSlot(Player player)",
    "this.cubeeItemService.purgeOutsideSystemSlot(playerInventory, systemSlot);",
    "playerInventory.setItem(systemSlot, this.cubeeItem());",
    "player.updateInventory();"
)

foreach ($snippet in $required) {
    if (-not $Plugin.Contains($snippet)) {
        throw "Creative Cubee recovery missing required behavior: $snippet"
    }
}

$restoreStart = $Plugin.IndexOf("private boolean restoreCubeeIfSlotEmpty(Player player)")
$restoreEnd = $Plugin.IndexOf("private boolean hasAnyCubeeItem", $restoreStart)
$placementStart = $Plugin.IndexOf("boolean occupiedByOtherItem =", $restoreEnd)
$placementEnd = $Plugin.IndexOf("playerInventory.setItem(n, this.cubeeItem());", $placementStart)
if ($restoreStart -lt 0 -or $restoreEnd -lt 0 -or $placementStart -lt 0 -or $placementEnd -lt 0) {
    throw "Could not isolate Cubee slot-conflict lifecycle."
}

$restore = $Plugin.Substring($restoreStart, $restoreEnd - $restoreStart)
$placement = $Plugin.Substring($placementStart, $placementEnd - $placementStart)
if ($restore -match 'itemStack != null(?s).*?setCubeeVisible\(player, false\)' -or
    $placement.Contains("this.setCubeeVisible(player, false)")) {
    throw "Cubee slot conflict must not change the persistent visibility preference."
}

Write-Host "Backend Cubee Creative recovery contract OK"
