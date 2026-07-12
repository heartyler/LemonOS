param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAccessMetadataService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\access.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendAccessMetadataService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendAccessMetadataService",
    "private final Function<String, String> nameNormalizer",
    "private final Predicate<String> nameValidator",
    "BackendAccessMetadataService(Function<String, String> nameNormalizer, Predicate<String> nameValidator)",
    "List<String> accessHolderNames(Path accessFile)",
    "if (!Files.isRegularFile(accessFile, new LinkOption[0]))",
    "Files.readAllLines(accessFile, StandardCharsets.UTF_8)",
    "if (stripped.equals(`"admins:`"))",
    "if (!stripped.startsWith(`"-`"))",
    "String name = this.nameNormalizer.apply(stripped.substring(1).trim())",
    "if (!admins || !this.nameValidator.test(name))",
    "names.add(name)",
    "catch (IOException exception)",
    "return List.of()",
    "return names.stream().distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList()"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendAccessMetadataService missing required access metadata contract text: $required"
    }
}

foreach ($required in @(
    "private BackendAccessMetadataService accessMetadataService",
    "this.accessMetadataService = new BackendAccessMetadataService(this::normalizeAccessName, this::safeAdminName)",
    "private List<String> accessHolderNames()",
    "this.runtimeLayout.dataFile(`"access.yml`")",
    "return this.accessMetadataService.accessHolderNames(file.toPath())",
    "private String normalizeAccessName(String string)",
    "private boolean safeAdminName(String string)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendAccessMetadataService: $required"
    }
}

foreach ($forbidden in @(
    "boolean bl = false;`r`n            for (String string : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8))",
    "if (string3.equals(`"admins:`"))",
    "string3.substring(1).trim()",
    "return arrayList.stream().distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList()"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend access metadata parsing detail: $forbidden"
    }
}

if ($template -notmatch "(?m)^version:\s*`"3.0`"\s*$" -or $template -notmatch "(?m)^admins:\s*$") {
    throw "Access template no longer preserves version/admins schema."
}

Write-Host "LemonOS backend access metadata contract tests passed."
