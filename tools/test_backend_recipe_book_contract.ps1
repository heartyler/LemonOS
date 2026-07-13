param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"

$source = Join-Path $Root "src\main\java\dev\lemonos"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $source "LemonOSPlugin.java")
$config = Get-Content -Raw -LiteralPath (Join-Path $source "BackendRecipeBookConfig.java")
$service = Get-Content -Raw -LiteralPath (Join-Path $source "BackendRecipeUnlockService.java")
$migration = Get-Content -Raw -LiteralPath (Join-Path $source "BackendFeatureConfigMigrationService.java")
$orchestrator = Get-Content -Raw -LiteralPath (Join-Path $source "BackendConfigMigrationOrchestrator.java")
$recipes = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\recipes.yml")
$survival = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\survival.yml")

foreach ($snippet in @(
    'final class BackendRecipeBookConfig',
    '!"survival".equals(serverId) && !"creative".equals(serverId)',
    'this.source.isBoolean(path) && this.source.getBoolean(path)'
)) { if (-not $config.Contains($snippet)) { throw "Recipe Book typed config missing: $snippet" } }

foreach ($snippet in @(
    'final class BackendRecipeUnlockService',
    'LinkedHashSet<NamespacedKey>',
    'recipe instanceof Keyed keyed',
    'player.discoverRecipes(keys)'
)) { if (-not $service.Contains($snippet)) { throw "Recipe unlock service missing: $snippet" } }

foreach ($snippet in @(
    'RecipeMigration migrateRecipes',
    'survival.recipe-book.unlock-all',
    'recipe-book.unlock-all.survival',
    'recipe-book.unlock-all.creative',
    'record RecipeMigration(boolean recipesChanged, boolean retireLegacySurvivalSection)'
)) { if (-not $migration.Contains($snippet)) { throw "Recipe config migration missing: $snippet" } }

foreach ($snippet in @(
    'boolean recipesSaved = this.save(target.recipesFile(), target.recipes(), recipeMigration.recipesChanged())',
    'if (recipesSaved && recipeMigration.retireLegacySurvivalSection()',
    'target.survival().set("survival.recipe-book", null)',
    'return new MigrationResult(hudSaved, atmosphereSaved, recipesSaved)'
)) { if (-not $orchestrator.Contains($snippet)) { throw "Recipe migration is missing guarded retirement: $snippet" } }

foreach ($snippet in @(
    'new BackendRecipeBookConfig(this.recipes)',
    'this.recipesConfigReady = migrationResult.recipesReady()',
    'private void unlockConfiguredRecipes(Player player)',
    'this.recipeBookConfig.unlockAll(this.currentServer.proxyName)',
    'this.recipeUnlockService.unlockAll(player, Bukkit.recipeIterator())',
    'this.logOperationFailure("recipe-book-" + this.currentServer.proxyName'
)) { if (-not $plugin.Contains($snippet)) { throw "Recipe Book integration missing: $snippet" } }

if (([regex]::Matches($plugin, [regex]::Escape('this.unlockConfiguredRecipes(player);')).Count) -ne 2) {
    throw "Recipe Book must run through both identity completion paths exactly once."
}
foreach ($removed in @('unlockSurvivalRecipes', 'survival.recipe-book.unlock-all', 'survival.recipe-book.silent')) {
    if ($plugin.Contains($removed)) { throw "Legacy Recipe Book plugin behavior remains: $removed" }
}
if ($survival -match '(?m)^  recipe-book:') { throw "survival.yml still owns Recipe Book policy." }
if ($recipes -notmatch '(?ms)^recipe-book:\s+unlock-all:\s+survival: true\s+creative: true') {
    throw "recipes.yml must enable unlock-all for Survival and Creative."
}
if ($recipes -match '(?m)^\s+lobby:') { throw "recipes.yml must not enable Recipe Book unlock-all for Lobby." }

Write-Host "Backend Recipe Book contract OK"
