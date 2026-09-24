# CLAUDE.md - Instruction Manual for Claude Code / Gemini

## Project Overview
Arvo is an advanced Android application designed to track internet consumption (mobile and Wi-Fi), manage global and per-app daily quotas, simulate and predict usage, export consumption logs, and block ads and internet access when quotas are reached.

## Critical Rules
- **CRITICAL:** Always check the `docs/data-dictionary.md` before naming any new field, column, or table.
- Use `snake_case` for all database naming conventions (tables and columns).
- Maintain robust privacy: ensure all user data and sensitive information are processed securely with strict local compliance and optional encrypted sync.

## Build & Test Commands
- Build Debug APK: `./gradlew assembleDebug`
- Build Release APK: `./gradlew assembleRelease`
- Run Unit Tests: `./gradlew test`
- Run Instrumentation Tests: `./gradlew connectedAndroidTest`

## Code Style Guidelines
- **Language:** Kotlin (Jetpack Compose for UI, Coroutines/Flow for async processing).
- **Architecture:** MVVM + Clean Architecture principles (Domain, Data, UI layers).
- **Tone & Copy:** Formal and bold.
- **Typography & Theming:** Custom typography featuring "Maiandra GD" as the primary font, supporting user-selectable font options and custom dynamic themes.
