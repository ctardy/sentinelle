# Sentinelle

> Android privacy & digital sovereignty audit — guided, local, zero tracking, zero network.

Sentinelle walks you through your Android phone's settings, step by step, to help you take back control over your personal data: advertising ID, sovereign DNS, voice assistants, OEM diagnostics, app permissions, and more.

The app is **autonomous**, **free**, and **collects no data**. No backend, no account, no funnel to another product. The Android APK has no `INTERNET` permission and never opens a network connection. Updates to the audit checks ship via Play Store releases.

*Version française de ce README : [README.fr.md](README.fr.md).*

## Contents

- [What it does](#what-it-does)
- [What it is NOT](#what-it-is-not)
- [Privacy guarantees](#privacy-guarantees)
- [Repository layout](#repository-layout)
- [Requirements](#requirements)
- [Build the Android app](#build-the-android-app)
- [Build the website](#build-the-website)
- [Knowledge base](#knowledge-base)
- [Supported devices, languages, countries](#supported-devices-languages-countries)
- [Tech stack](#tech-stack)
- [Contributing](#contributing)
- [License](#license)
- [Further documentation](#further-documentation)

## What it does

1. Detects what can be identified **without invasive permissions**: `Build.MANUFACTURER`, `Build.MODEL`, `Build.VERSION.SDK_INT`, presence of OEM packages, advertising ID status.
2. Walks the user through a guided, step-by-step audit. For each check:
   - explains the concrete risk,
   - opens the relevant Android Settings screen directly via an `Intent`,
   - offers a manual fallback path,
   - lets the user mark the check as done.
3. Computes a **sovereignty score** weighted by severity (critical = 5, important = 3, recommended = 1).

Each check is purely informational plus a one-tap jump into the system Settings. Sentinelle never flips a setting on your behalf.

## What it is NOT

- **Not a firewall** — see [NetGuard](https://netguard.me/) or [TrackerControl](https://trackercontrol.org/).
- **Not an APK tracker scanner** — see [Exodus Privacy](https://exodus-privacy.eu.org/).
- **Not a VPN**.
- **Not a paid product**.
- **Not a lead magnet** — Sentinelle is not a front for any other product or service.

## Privacy guarantees

- **Zero tracking**: no analytics, no crash reporting (unless you explicitly opt in, which currently doesn't exist), no Google Play Services beyond what the Android framework itself uses.
- **Zero network** on the APK: the manifest declares no permissions whatsoever. `INTERNET` is absent.
- **Knowledge base embedded at build time**: all audit points, texts and `Intent`s are shipped inside the APK as JSON assets. Updating the checks requires publishing a new release — we accept that cost to keep the app offline.
- **Cloud backup disabled** (`android:allowBackup="false"`) and `data_extraction_rules.xml` excludes everything from cloud backup and device-to-device transfer.
- **No account, no telemetry, no identifier**. The only persistent state is local preferences via `DataStore` (selected profile + country, checks marked as done).

## Repository layout

```
sentinel/
├── android/                          # Kotlin + Jetpack Compose + Material 3 app
│   ├── app/src/main/kotlin/app/sentinelle/
│   │   ├── data/                     # KB reader, device detector, DataStore repos
│   │   ├── domain/                   # typed models + sovereignty score
│   │   └── ui/                       # Compose screens (onboarding, audit, detail)
│   └── app/src/main/res/             # i18n strings (fr / en / es / de), themes
├── web/                              # Astro static website (public mirror of the KB)
│   └── src/pages/[lang]/             # one tree per supported UI language
├── knowledge-base/                   # source of truth — versioned JSON
│   ├── schema.json                   # JSON Schema for profiles
│   ├── schema-dns.json               # JSON Schema for DNS options
│   └── v1/
│       ├── index.json                # profiles + countries catalog
│       ├── dns/                      # one file per country (fr, de, es, gb, eu)
│       └── profiles/                 # one file per {profileId}.{lang}
├── docs/                             # internal docs (in French)
├── AGENTS.md                         # project rules (in French)
├── README.md                         # this file
└── README.fr.md                      # French version of this README
```

## Requirements

- **Android**: Android Studio Ladybug+ or standalone Gradle 9.4.1. JDK 21 (as the JVM that runs Gradle). Android SDK (compileSdk 36, build-tools 35+).
- **Website**: Node.js ≥ 20.

On first clone, create `android/local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
```

This file is git-ignored on purpose (machine-specific).

## Build the Android app

```bash
cd android
JAVA_HOME="/path/to/jdk-21" ./gradlew assembleDebug
./gradlew --stop            # always stop the Gradle daemon afterwards
```

The debug APK lands in `app/build/outputs/apk/debug/app-debug.apk`.

### Windows note

Use `cmd.exe /c ".\gradlew.bat assembleDebug"` from Git Bash:

```bash
cd /c/dev/projects/sentinel/android
JAVA_HOME="C:\dev\softs\java\jdk-21.0.3+9" cmd.exe //c ".\gradlew.bat assembleDebug"
./gradlew --stop
```

### Versioning

`android/version.properties` holds `versionCode` and `versionName`. Two helper tasks are provided:

```bash
./gradlew bumpVersionCode    # increment versionCode by 1
./gradlew bumpVersionName    # bump minor versionName + versionCode
```

Always bump before producing a release build.

## Build the website

```bash
cd web
npm install
npm run dev        # local dev server at http://localhost:4321/
npm run build      # static build → dist/
npm run preview    # preview the built site
```

The site reads the knowledge base directly from `../knowledge-base/v1/` at build time (Node `fs`), so there's no content duplication. Every supported language gets its own static tree (`/fr/...`, `/en/...`, `/es/...`, `/de/...`) with `prefixDefaultLocale: true`.

## Knowledge base

All audit content lives under `knowledge-base/v1/`. It is **never hardcoded** in Kotlin or TypeScript.

### Structure

- `index.json` — lists available profiles, supported languages per profile, DNS-capable countries, default country.
- `profiles/{profileId}.{lang}.json` — one file per language, identical structure across languages. Only labels and explanations change; `checkId`, `categoryId`, `intent.action`, `autoDetect` and `dnsOptionsCountryFile` stay stable.
- `dns/{country}.json` — list of sovereign DNS operators recommended for a given country (lowercase ISO 3166-1 alpha-2).

### Adding a new profile

1. Create `profiles/{new-profile}.fr.json` using `profiles/android-generic.fr.json` as a reference.
2. Declare it in `index.json` under `profiles`.
3. Translate into EN / ES / DE by duplicating the file and changing only the user-facing strings.
4. Make sure it validates against `schema.json`.

### Adding a country's DNS options

1. Create `dns/{country}.json` following `schema-dns.json`.
2. Add the country code to `dnsCountries` in `index.json`.

### Adding a language

Translate every `profiles/*.json` for the new language. Update `languages` in `index.json` for each profile, and add the language to the Android resource qualifiers (`values-{lang}/`) with the UI strings.

### Build-time sync (Android)

A Gradle task `syncKnowledgeBase` copies `knowledge-base/v1/**/*.json` into `android/app/src/main/assets/kb/v1/` before `mergeDebugAssets` / `mergeReleaseAssets`. The generated tree is git-ignored.

## Supported devices, languages, countries

- **Manufacturers**: generic Android (Pixel / AOSP), Samsung Galaxy (One UI).
- **Android versions**: 10 to 15 (API 29 to 35).
- **UI languages**: French (default / source of truth), English, Spanish, German.
- **Countries for local options**: France, Germany, Spain, United Kingdom, plus a generic "Europe" profile.

Extensible in all four dimensions — contributions welcome (see below).

## Tech stack

### Android

- Kotlin 2.3.20, Jetpack Compose + Material 3, AGP 9.0.1, Gradle 9.4.1.
- `minSdk` 29, `targetSdk` 35, `compileSdk` 36, JDK target 17.
- Dependencies: `kotlinx.serialization`, `navigation-compose`, `datastore-preferences`.
- No network library (no OkHttp, no Retrofit). No DI framework.

### Website

- [Astro 5](https://astro.build/) with four language trees (fr / en / es / de), `prefixDefaultLocale: true`.
- Zero JavaScript on the client. Pure static HTML.
- Mobile-first, dark theme, no external CDN.

## Contributing

The simplest, most impactful contributions:

- **Translate a profile** into an additional UI language.
- **Add a country's DNS options** — especially if you know good local sovereign resolvers.
- **Add a new profile** for another OEM (Xiaomi / MIUI, Oppo / ColorOS, Motorola, etc.).
- **Correct a settings path** that drifted between Android versions.

Please keep:

- `checkId`, `categoryId`, `intent.action`, `autoDetect` and `dnsOptionsCountryFile` **stable across translations**.
- Commits in French (see `AGENTS.md`).
- No new dependency without a clear justification — the app must stay network-free and small.

Issue templates and a `CONTRIBUTING.md` may appear later.

## License

**TBD.** No license file is committed yet, which means default copyright applies (all rights reserved). A permissive license (likely MIT or Apache-2.0) will be added before any external contribution is merged. If you have a preference, open an issue.

## Further documentation

- [`AGENTS.md`](AGENTS.md) — absolute rules, project description, architecture (French).
- [`docs/tickets.md`](docs/tickets.md) — backlog and progress (French).
- [`docs/prompt-demarrage.md`](docs/prompt-demarrage.md) — bootstrap prompt for new contributor / agent sessions (French).
- [`knowledge-base/schema.json`](knowledge-base/schema.json) and [`schema-dns.json`](knowledge-base/schema-dns.json) — JSON Schema definitions.
