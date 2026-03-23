# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Test
./gradlew test                  # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests (requires device/emulator)

# Clean
./gradlew clean
```

On Windows, use `gradlew` instead of `./gradlew`.

## Architecture

LeetFlow is a single-module Android app (MVVM, no repository layer in practice).

**Data flow:** UI (Compose) → ViewModel (StateFlow) → Remote/Local services directly

**Layers:**
- `ui/` — Jetpack Compose screens, one folder per feature (e.g. `screens/leetcodestats/`). Each feature has `Screen.kt`, `ViewModel.kt`, and `State.kt`.
- `data/remote/` — Retrofit + GraphQL (`LeetCodeGraphQLService`), Gemini AI (`GeminiApiService`). No wrapper repositories; ViewModels call services directly.
- `data/local/` — Room DB (`LeetFlowDatabase`, single entity `RecallNote` for AI flashcards), `SecureStorageManager` (wraps `EncryptedSharedPreferences` for Gemini API key storage).
- `ui/widget/` — Home screen widget (`LeetCodeStatsWidget`) uses `RemoteViews` (not Compose); draws concentric ring arcs for Easy/Medium/Hard progress.
- `ui/navigations/` — `Screen.kt` sealed class + `NavigationGraph.kt` with `NavHost`.

**UI state pattern:** Each screen uses a sealed class with `Idle`, `Loading`, `Success`, `Error` variants collected via `collectAsState()`.

## Key Technical Details

- **KSP** (not KAPT) for Room code generation — annotation processor config is under `ksp {}` in `app/build.gradle.kts`
- **Room** uses `fallbackToDestructiveMigration()` — schema changes will wipe data in development
- **Gemini model**: `gemini-2.5-flash` — referenced in `GeminiApiService.kt`
- **LeetCode GraphQL**: two parallel queries (profile + contest), contest failure is non-fatal
- **SecureStorageManager** auto-reinitializes on KeyStore corruption (signature change)
- ViewModels extend `AndroidViewModel` for Application context access; no DI framework
- Widget uses `goAsync()` in `onReceive` to avoid ANR on network calls
- In-app updates use flexible update type (Play Core)

## Commit Style

- Prefix: `feat:`, `fix:`, `refactor:`, `chore:`
- Example: `fix: use green for submission heatmap cells instead of orange`
- Do NOT add `Co-Authored-By` trailers