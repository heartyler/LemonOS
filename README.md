# LemonOS

LemonOS is the backend and Velocity integration layer used by the Honey Minecraft 26.2 runtime. This repository contains the Java sources, canonical runtime presets, migration and deployment tooling, and behavioral contract suite.

## Repository layout

- `src/main/java` — Paper backend plugin
- `src_proxy` — Velocity proxy plugin
- `src/main/resources/defaults/LemonOS` — presets bundled in the backend JAR
- `templates/runtime` — canonical runtime configuration and data templates
- `tools` — validation, migration, runtime initialization, and deployment tools

## Requirements

- Windows PowerShell
- JDK 26
- Network access for the pinned compile-time dependency restore

Restore the small compile-time test dependencies after a fresh clone:

```powershell
.\tools\restore_test_dependencies.ps1
```

## Validate

Run the behavioral and source contracts:

```powershell
.\tools\run_lemonos_contracts.ps1
```

Build both artifacts and run the legacy-config migration staging check:

```powershell
.\build_lemonos.ps1 -JdkRoot 'C:\Program Files\Java\jdk-26.0.1'
```

Generated artifacts:

- `build/libs/lemonos.jar`
- `build/libs/lemonos_proxy.jar`

Exercise the complete runtime initialization, verification, artifact deployment, and backup flow in an isolated disposable runtime:

```powershell
.\tools\run_runtime_integration_tests.ps1
```

## Runtime workflow

Runtime reset and deployment tools require an offline Honey runtime with a valid `deployment.json` manifest:

```powershell
.\tools\initialize_lemonos_runtime.ps1 -RuntimeRoot 'C:\path\to\runtime' -ResetConfig -ResetData
.\tools\deploy_lemonos_artifacts.ps1 -RuntimeRoot 'C:\path\to\runtime'
.\tools\verify_lemonos_runtime.ps1 -RuntimeRoot 'C:\path\to\runtime'
```

The deployment tools refuse to reset or replace LemonOS files while known Honey runtime ports are active.

## Continuous integration

GitHub Actions runs dependency verification, all contracts, the standalone backend/proxy build, isolated runtime integration tests, migration staging, and patch-hygiene checks on pushes and pull requests. Successful runs publish both JARs as short-lived workflow artifacts.

## Releases

`VERSION` is the single source for the Paper descriptor, Velocity descriptor, and proxy runtime version. To publish a release, update `VERSION` through a pull request and push the matching tag after it merges:

```powershell
git tag v1.0.0
git push origin v1.0.0
```

The release workflow rejects mismatched tags, reruns all validation, and publishes both JARs with `SHA256SUMS.txt` to GitHub Releases.
