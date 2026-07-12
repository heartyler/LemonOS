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
- A Honey 26.2 development runtime containing Paper and Velocity dependencies

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
.\build_lemonos.ps1 `
  -RuntimeRoot 'C:\path\to\honey-26.2-dev' `
  -JdkRoot 'C:\Program Files\Java\jdk-26.0.1'
```

Generated artifacts:

- `build/libs/lemonos.jar`
- `build/libs/lemonos_proxy.jar`

## Runtime workflow

Runtime reset and deployment tools require an offline Honey runtime with a valid `deployment.json` manifest:

```powershell
.\tools\initialize_lemonos_runtime.ps1 -RuntimeRoot 'C:\path\to\runtime' -ResetConfig -ResetData
.\tools\deploy_lemonos_artifacts.ps1 -RuntimeRoot 'C:\path\to\runtime'
.\tools\verify_lemonos_runtime.ps1 -RuntimeRoot 'C:\path\to\runtime'
```

The deployment tools refuse to reset or replace LemonOS files while known Honey runtime ports are active.
