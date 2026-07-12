param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendOnlineNamesService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\online.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendOnlineNamesService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendOnlineNamesService",
    "private static final long ONLINE_SNAPSHOT_MAX_AGE_MILLIS = 15000L",
    "private final Function<String, String> nameNormalizer",
    "private final Predicate<String> nameValidator",
    "BackendOnlineNamesService(Function<String, String> nameNormalizer, Predicate<String> nameValidator)",
    "List<String> onlineNames(Path onlineFile, long nowMillis)",
    "if (!Files.isRegularFile(onlineFile, new LinkOption[0]))",
    "return new ArrayList<String>()",
    "Files.readAllLines(onlineFile, StandardCharsets.UTF_8)",
    "if (!stripped.startsWith(`"updated:`"))",
    "updated = Long.parseLong(stripped.substring(`"updated:`".length()).trim())",
    "if (updated <= 0L || nowMillis - updated > ONLINE_SNAPSHOT_MAX_AGE_MILLIS)",
    "if (!stripped.startsWith(`"- name:`"))",
    "String name = this.nameNormalizer.apply(stripped.substring(`"- name:`".length()).trim())",
    "if (!this.nameValidator.test(name))",
    "names.add(name)",
    "catch (IOException | NumberFormatException exception)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendOnlineNamesService missing required online names contract text: $required"
    }
}

foreach ($required in @(
    "private BackendOnlineNamesService onlineNamesService",
    "this.onlineNamesService = new BackendOnlineNamesService(this::normalizeAccessName, this::safeAdminName)",
    "private List<String> networkOnlineNames()",
    "this.runtimeLayout.dataFile(`"online.yml`")",
    "return this.onlineNamesService.onlineNames(file.toPath(), System.currentTimeMillis())",
    "private String normalizeAccessName(String string)",
    "private boolean safeAdminName(String string)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendOnlineNamesService: $required"
    }
}

foreach ($forbidden in @(
    "private static final long ONLINE_SNAPSHOT_MAX_AGE_MILLIS",
    "Long.parseLong(string.substring(`"updated:`".length()).trim())",
    "System.currentTimeMillis() - l > 15000L",
    "string.substring(`"- name:`".length()).trim()"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend online names parsing detail: $forbidden"
    }
}

if ($backend -match "private List<String> networkOnlineNames\(\)\s*\{(?s).*?Files\.readAllLines\(file\.toPath\(\), StandardCharsets\.UTF_8\).*?\n\s*\}") {
    throw "LemonOSPlugin networkOnlineNames still reads online.yml directly."
}

if ($template -notmatch "(?m)^updated:\s*0\s*$") {
    throw "Online template no longer preserves updated default."
}

Write-Host "LemonOS backend online names contract tests passed."
