param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPasscodeInputService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPasscodeInputService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPasscodeInputService",
    "private final BackendPasscodeLayout layout;",
    "BackendPasscodeInputService(BackendPasscodeLayout layout)",
    "Integer digit(int slot)",
    "return this.layout.digit(slot);",
    "EnterState enterState(String passcode, boolean overflow)",
    "if (overflow)",
    "return EnterState.TOO_LONG;",
    "if (value.isEmpty())",
    "return EnterState.HIDDEN;",
    "if (value.length() < 4)",
    "return EnterState.TOO_SHORT;",
    "return EnterState.READY;",
    "LoginMode resolveLoginMode(LoginMode current, boolean registeredWithoutResetGrant, boolean resetRequestExists, boolean resetGrantExists)",
    "mode = registeredWithoutResetGrant ? LoginMode.LOGIN : LoginMode.CREATE_PASSCODE;",
    "if (resetRequestExists && mode == LoginMode.LOGIN)",
    "mode = LoginMode.RESET_WAITING;",
    "if (resetGrantExists)",
    "mode = LoginMode.CREATE_PASSCODE;",
    "if (mode == LoginMode.RESET_WAITING && !resetRequestExists)",
    "mode = LoginMode.LOGIN;",
    "enum EnterState",
    "enum LoginMode"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPasscodeInputService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendPasscodeInputService passcodeInputService;",
    "BackendPasscodeLayout backendPasscodeLayout = new BackendPasscodeLayout();",
    "this.passcodeInputService = new BackendPasscodeInputService(backendPasscodeLayout);",
    "this.passcodeInputService.resolveLoginMode(",
    "this.loginMode(identityInput)",
    "this.identityRegistered(identityKey) && !resetGrantExists",
    "resetRequestExists",
    "resetGrantExists",
    "private BackendPasscodeInputService.LoginMode loginMode(IdentityInput identityInput)",
    "private IdentityInput identityInput(BackendPasscodeInputService.LoginMode loginMode)",
    "this.setLoginEnterButton(inventory, this.passcodeInputService.enterState(string, overflow), creating);",
    "switch (enterState)",
    "String actionLabel = creating",
    "this.messageText(`"labels.create`", `"Create`")",
    "this.messageText(`"labels.enter`", `"Sign in`")",
    "case TOO_LONG -> inventory.setItem(Ui.Login.ENTER.slot(), this.menuItem(Material.GRAY_STAINED_GLASS_PANE, actionLabel, this.resultText(`"too-long`", `"too long.`")));",
    "case HIDDEN -> inventory.setItem(Ui.Login.ENTER.slot(), null);",
    "case TOO_SHORT -> inventory.setItem(Ui.Login.ENTER.slot(), this.menuItem(Material.GRAY_STAINED_GLASS_PANE, actionLabel, this.resultText(`"too-short`", `"too short.`")));",
    "case READY -> inventory.setItem(Ui.Login.ENTER.slot(), this.menuItem(Material.LIME_STAINED_GLASS_PANE, actionLabel, `"go in.`"));",
    "return this.passcodeInputService.digit(n);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPasscodeInputService: $snippet"
    }
}

Write-Host "Backend passcode input contract OK"
