param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPasscodeDisplayService.java"
$LayoutPath = Join-Path $Root "src\main\java\dev\lemonos\BackendPasscodeLayout.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPasscodeDisplayService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Layout = Get-Content -Raw -LiteralPath $LayoutPath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPasscodeDisplayService",
    "private final BackendPasscodeLayout layout;",
    "BackendPasscodeDisplayService(BackendPasscodeLayout layout)",
    "PasscodeDisplay display(boolean creating, String passcode, String status, BackendPasscodeInputService.EnterState enterState)",
    "title(creating, value, status)",
    "!value.isEmpty()",
    "!creating",
    "TitlePlan title(boolean creating, String passcode, String status)",
    "if (status != null)",
    "if (value.isEmpty())",
    "Math.min(value.length(), 8)",
    "List<DigitButton> digitButtons()",
    "this.layout.digitButtons()",
    "record PasscodeDisplay(",
    "record TitlePlan(TitleKind kind, String status, int maskLength)",
    "record DigitButton(int slot, String label)",
    "enum TitleKind",
    "CREATE_EMPTY",
    "CREATE_STATUS",
    "CREATE_MASKED",
    "LOGIN_EMPTY",
    "LOGIN_STATUS",
    "LOGIN_MASKED"
)

foreach ($required in @(
    "private static final int[] DIGIT_SLOTS = new int[]{2, 3, 4, 11, 12, 13, 20, 21, 22, 23};",
    "private static final String[] DIGIT_LABELS = new String[]{`"1`", `"2`", `"3`", `"4`", `"5`", `"6`", `"7`", `"8`", `"9`", `"0`"};"
)) {
    if (-not $Layout.Contains($required)) { throw "BackendPasscodeLayout missing exact layout: $required" }
}

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPasscodeDisplayService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendPasscodeDisplayService passcodeDisplayService;",
    "this.passcodeDisplayService = new BackendPasscodeDisplayService(backendPasscodeLayout);",
    "BackendPasscodeDisplayService.PasscodeDisplay display = this.passcodeDisplayService.display(",
    "this.passcodeTitleStatuses.get(player.getUniqueId())",
    "this.passcodeInputService.enterState(string, false)",
    "this.loginTitle(display.title())",
    "this.setLoginDigits(inventory, display.digits(), Material.BLACK_STAINED_GLASS_PANE);",
    "if (display.showClear())",
    "if (display.showReset())",
    "this.setLoginEnterButton(inventory, display.enterState(), bl);",
    "private Component loginTitle(BackendPasscodeDisplayService.TitlePlan titlePlan)",
    "case CREATE_EMPTY -> this.statusTitle(this.promptText(`"create-passcode`", `"Create Passcode`"), this.promptText(`"passcode-length`", `"4-8 numbers`"));",
    "case LOGIN_EMPTY -> Component.text((String)this.promptText(`"enter-passcode`", `"Enter Passcode`"), (TextColor)HoneyPalette.DEFAULT_WHITE);",
    "case CREATE_MASKED ->",
    "case LOGIN_MASKED ->",
    "titlePlan.maskLength()",
    "private void setLoginEnterButton(Inventory inventory, BackendPasscodeInputService.EnterState enterState, boolean creating)",
    '? this.messageText("labels.create", "Create")',
    ': this.messageText("labels.enter", "Sign in");',
    "private void setLoginDigits(Inventory inventory, List<BackendPasscodeDisplayService.DigitButton> buttons, Material material)",
    "inventory.setItem(button.slot(), this.menuItem(material, button.label(), null));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPasscodeDisplayService: $snippet"
    }
}

Write-Host "Backend passcode display contract OK"
