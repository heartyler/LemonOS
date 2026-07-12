param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\admin\BackendAdminPagedSelectionClickService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    "public final class BackendAdminPagedSelectionClickService",
    "SelectionResult action(int clickedSlot, int backSlot, int nextPageSlot, Map<Integer, String> slotKeys)",
    "record SelectionResult(SelectionAction action, String selectedName)",
    "enum SelectionAction { NONE, BACK, NEXT_PAGE, SELECT }"
)) { if (-not $service.Contains($required)) { throw "Admin paged selection contract missing: $required" } }
if ($plugin -notmatch 'private BackendAdminPagedSelectionClickService adminPagedSelectionClickService' -or
    ([regex]::Matches($plugin, 'this\.adminPagedSelectionClickService\.action\(').Count -ne 4)) {
    throw "Admin key selection flows are not consolidated."
}
foreach ($required in @(
    "case SELECT -> this.openNextTick(() -> this.openAdminTakeKeyConfirm(player, clickResult.selectedName(), pageIndex));",
    "case SELECT -> this.openNextTick(() -> this.openAdminKeyGive(player, clickResult.selectedName(), pageIndex));",
    "this.openAdminTakeKeyConfirm(player, clickResult.selectedName(), 0)",
    "this.openAdminKeyGive(player, clickResult.selectedName(), 0)",
    "Ui.Shared.FORM_BACK.slot()",
    "Ui.Care.KEY_FIND.slot()",
    "Ui.Care.FIND.slot()"
)) { if (-not $plugin.Contains($required)) { throw "Admin paged selection wiring missing: $required" } }
Write-Host "Backend consolidated admin paged selection contract OK"
