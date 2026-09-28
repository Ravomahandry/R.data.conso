# AGENTS.md

Arvo is a single-module Android app (`io.arvo.dataconso`): tracks internet data usage, enforces global/per-app quotas via VpnService + AccessibilityService, blocks ads/traffic at quota, and does on-device AI prediction. Built on Windows (PowerShell). More spec context: `docs/` (app-spec.md, CLAUDE.md, data-dictionary.md).

## Build & verify
- Build: `.\gradlew.bat assembleDebug` (PowerShell; `./gradlew` works in bash).
- Release: `assembleRelease` runs but produces an **unsigned** APK — there is no `signingConfig` in `app/build.gradle.kts`.
- **No test sources exist** (`app/src/test`, `app/src/androidTest`), so `test`/`connectedAndroidTest` are no-ops; JUnit/instrumentation deps are declared but unused.
- No lint/formatter config (no ktlint/spotless). Use `assembleDebug` compile as your sanity check.
- Stack pinned in root `build.gradle.kts` / wrapper: Gradle 9.7.1, AGP 9.4.1, Kotlin 2.2.10, JDK 17 target (`gradle/gradle-daemon-jvm.properties` forces a JDK 21 daemon toolchain). First build downloads NDK 27.2.12479018 + CMake 3.22.1 (auto-provisioned) and is slow.

## Structure & conventions
- Single `:app` module; **no version catalog** — dependency versions are pinned inline in `app/build.gradle.kts`.
- Root package is flat: most screens/services/viewmodels live directly under `io.arvo.dataconso`; layered code is under `di/`, `domain/`, `repository/`, `network/`, `ui/`, `ai/`, `backend/`, `security/`, `util/`. Put new screens in `ui/`.
- **KSP version must match Kotlin exactly** (Kotlin 2.2.10 ↔ KSP `2.2.10-2.0.2`). Bump both together.
- Native: `app/src/main/cpp/` builds `arvo_native`. Its JNI exports are hardcoded to the Kotlin class FQN (e.g. `Java_io_arvo_dataconso_network_RealPacketInterceptor_*`) — renaming/moving those classes requires editing `native-lib.cpp`.

## Database (Room, SQLCipher-encrypted)
- DB name `arvo_v5_final.db`, current version 32. Actual tables: `app_settings`, `history`, `simulations`, `app_quotas` — defined in `app/src/main/java/io/arvo/dataconso/AppDatabase.kt`.
- `docs/data-dictionary.md` lists tables that no longer exist (`users`, `quotas`, `usage_logs`); treat it as advisory for the **snake_case naming rule only**, and align new fields with the real code.
- Bumping `@Database(version = …)`: add a `Migration` and register it, or data is wiped (`fallbackToDestructiveMigration()` is currently enabled).
- `AppDatabase.getDatabase()` deletes the DB file if the encryption-key check throws — keep the helper/encryption wiring intact.

## Firebase
- `app/google-services.json` is committed and **required** (google-services plugin applied; BOM 32.7.0: auth, database, analytics). Build fails without it; don't "clean" it.

## Gotchas
- `sauvegarde-release-key.jks` is a **release keystore committed to git** but not referenced by any `signingConfig`. Don't commit new credentials; if you wire up signing, flag the committed key for rotation.
- `app/.cxx/` build artifacts are tracked and not gitignored — native builds dirty `git status`. Don't stage `.cxx/` changes.
- Code comments and docs are largely French (the codebase mixes French/English); UI strings are localized (values include `mg`, `ar`, `zh`, etc.). Don't strip or "translate" the French comments.