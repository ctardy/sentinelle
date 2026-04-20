# Tickets — Sentinelle

> Inventaire des travaux restants. Dernière mise à jour : 2026-04-20.

## Légende des états

- **À faire** — Non démarré.
- **En cours** — Travail amorcé, pas terminé.
- **Bloqué** — Attend une décision, une dépendance ou une ressource externe.
- **Fait** — Terminé, validé.

## Inventaire

| ID       | Titre                                                           | État     | Avancement | Dépend de |
| -------- | --------------------------------------------------------------- | -------- | ---------: | --------- |
| KB-01    | Traduire profil `android-generic` en ES                         | Fait     |       100 % | —         |
| KB-02    | Traduire profil `android-generic` en DE                         | Fait     |       100 % | —         |
| KB-03    | Créer profil `samsung-oneui` en FR                              | Fait     |       100 % | —         |
| KB-04    | Traduire profil `samsung-oneui` en EN                           | Fait     |       100 % | KB-03     |
| KB-05    | Traduire profil `samsung-oneui` en ES                           | Fait     |       100 % | KB-03     |
| KB-06    | Traduire profil `samsung-oneui` en DE                           | Fait     |       100 % | KB-03     |
| KB-07    | Rédiger options DNS souverains pour ES                          | Fait     |       100 % | —         |
| KB-08    | Rédiger options DNS souverains pour GB                          | Fait     |       100 % | —         |
| KB-09    | Rédiger options DNS par défaut « EU » (profil générique)        | Fait     |       100 % | —         |
| AND-01   | Choisir le nom de package Android (`app.sentinelle`)            | Fait     |       100 % | —         |
| AND-02   | Scaffolder le projet Gradle (Kotlin + Compose + Material 3)     | Fait     |       100 % | AND-01    |
| AND-03   | Structure en couches (data / domain / ui)                       | Fait     |       100 % | AND-02    |
| AND-04   | Ressources i18n (`strings_*.xml` par domaine, FR/EN/ES/DE)      | Fait     |       100 % | AND-02    |
| AND-05   | Lecteur KB depuis assets (parsing JSON + modèles Kotlin)        | Fait     |       100 % | AND-03    |
| AND-06   | Détection device (`Build.MANUFACTURER`, `MODEL`, `SDK_INT`)     | Fait     |       100 % | AND-03    |
| AND-07   | Écran onboarding + sélection profil + pays                      | Fait     |       100 % | AND-05, AND-06 |
| AND-08   | Écran liste des checks par catégorie                            | Fait     |       100 % | AND-07    |
| AND-09   | Écran détail check avec ouverture d'Intents Réglages            | Fait     |       100 % | AND-08    |
| AND-10   | Persistance de l'avancement (DataStore, pas de Room finalement) | Fait     |       100 % | AND-03    |
| AND-11   | Score de souveraineté (header AuditScreen)                      | Fait     |       100 % | AND-10    |
| AND-12   | Sélecteur pays pour options locales (DNS)                       | À faire  |         0 % | AND-05    |
| AND-13   | Thème Material 3 + mode sombre cohérent avec le site            | À faire  |         0 % | AND-02    |
| AND-14   | Intégration captures d'écran dans les écrans de check           | À faire  |         0 % | AST-01, AND-09 |
| AND-15   | Incrémenter `android/version.properties` avant release          | À faire  |         0 % | AND-02    |
| AND-16   | Script Gradle : copie `knowledge-base/` → `assets/kb/` au build | Fait     |       100 % | AND-02    |
| WEB-01   | Remplir les pages DNS ES / GB / EU côté site                    | Fait     |       100 % | KB-07, KB-08, KB-09 |
| WEB-02   | Ajouter le profil `samsung-oneui` dans les pages Astro          | Fait     |       100 % | KB-03 à KB-06 |
| AST-01   | Captures d'écran par OEM × version Android (Pixel, Samsung)     | À faire  |         0 % | —         |

## Détail par ticket

### KB — Base de connaissances

#### KB-01 — Traduire profil `android-generic` en ES
- **État** : À faire — 0 %
- **Livrable** : `knowledge-base/v1/profiles/android-generic.es.json`, structure identique au FR/EN, validée par `schema.json`.

#### KB-02 — Traduire profil `android-generic` en DE
- **État** : À faire — 0 %
- **Livrable** : `knowledge-base/v1/profiles/android-generic.de.json`.

#### KB-03 — Créer profil `samsung-oneui` en FR
- **État** : À faire — 0 %
- **Portée** : checks spécifiques One UI (Samsung Account, Bixby, Customization Service, Samsung Push Service, Game Optimizing Service, diagnostics Samsung).
- **Livrable** : `knowledge-base/v1/profiles/samsung-oneui.fr.json`.

#### KB-04 / KB-05 / KB-06 — `samsung-oneui` en EN / ES / DE
- **État** : À faire — 0 %
- **Dépend de** : KB-03 (structure de référence).

#### KB-07 — DNS ES
- **État** : À faire — 0 %
- **Livrable** : `knowledge-base/v1/dns/es.json` (dns0.eu, Quad9, options locales si pertinentes).

#### KB-08 — DNS GB
- **État** : À faire — 0 %
- **Livrable** : `knowledge-base/v1/dns/gb.json`.

#### KB-09 — DNS EU (profil par défaut)
- **État** : À faire — 0 %
- **Livrable** : `knowledge-base/v1/dns/eu.json`. Sert de fallback quand le pays est inconnu.

### AND — Application Android

#### AND-01 — Nom de package
- **État** : Fait — 100 %
- **Décision** : `app.sentinelle`.

#### AND-02 — Scaffold Gradle + Compose + Material 3
- **État** : Fait — 100 %
- **Livrable** : projet Gradle/Kotlin/Compose/Material 3 dans `android/`, package `app.sentinelle`, minSdk 29, targetSdk 35, compileSdk 36, JDK target 17, AndroidManifest sans aucune permission, i18n FR/EN/ES/DE. Versions alignées sur `souvenirs` : Gradle 9.4.1, AGP 9.0.1, Kotlin 2.3.20, Compose BOM 2026.03.00. Wrapper copié depuis `souvenirs/android/`.
- **Build validé** : `./gradlew assembleDebug` → APK 29 MB dans `app/build/outputs/apk/debug/`.
- **Tâches utiles** : `bumpVersionCode`, `bumpVersionName` (incrémentation de `version.properties`).

#### AND-03 — Architecture en couches
- **État** : À faire — 0 %
- **Portée** : `data/` (KB, Room), `domain/` (modèles audit), `ui/` (Compose).

#### AND-04 — i18n Android
- **État** : À faire — 0 %
- **Portée** : `res/values/` (FR défaut), `values-en/`, `values-es/`, `values-de/`, découpage `strings_*.xml` par domaine (onboarding, audit, a11y…).

#### AND-05 — Lecteur KB depuis assets
- **État** : À faire — 0 %
- **Portée** : lecture des JSON depuis `assets/kb/v1/`, parseur → modèles Kotlin typés. Aucun fallback réseau.

#### AND-06 — Détection device
- **État** : À faire — 0 %
- **Portée** : `Build.MANUFACTURER`, `Build.MODEL`, `Build.VERSION.SDK_INT`, détection présence packages OEM, statut Advertising ID.

#### AND-07 — Onboarding + sélection profil + pays
- **État** : À faire — 0 %
- **Portée** : accueil, choix auto-suggéré selon `AND-06`, override manuel, sélection pays pour DNS.

#### AND-08 — Liste des checks par catégorie
- **État** : À faire — 0 %

#### AND-09 — Détail check + Intents Réglages
- **État** : À faire — 0 %
- **Portée** : ouverture directe des écrans Réglages via `Intent` (action + extras depuis la KB), bouton « J'ai fait ».

#### AND-10 — Persistance Room
- **État** : À faire — 0 %
- **Règle** : BDD jetable, pas de migration.

#### AND-11 — Écran final : score de souveraineté
- **État** : À faire — 0 %
- **Portée** : calcul pondéré par sévérité, récap, encouragement à relancer l'audit après changements de réglages. Écran terminal (pas de lead gen).

#### AND-12 — Sélecteur pays (DNS)
- **État** : À faire — 0 %

#### AND-13 — Thème Material 3 + dark mode
- **État** : À faire — 0 %

#### AND-14 — Intégration captures d'écran
- **État** : À faire — 0 %
- **Dépend de** : AST-01.

#### AND-15 — `version.properties`
- **État** : À faire — 0 %
- **Règle** : incrémenter avant chaque build release.

#### AND-16 — Script Gradle : copie KB vers assets
- **État** : À faire — 0 %
- **Portée** : tâche Gradle qui copie `knowledge-base/v1/*` vers `android/app/src/main/assets/kb/v1/` avant `processDebugResources` / `processReleaseResources`. Évite la duplication en dur dans le dépôt.

### WEB — Site Astro

#### WEB-01 — Pages DNS ES / GB / EU
- **État** : Fait — 100 %

#### WEB-02 — Profil `samsung-oneui` dans les pages
- **État** : Fait — 100 %
- **Note** : les pages Astro sont générées automatiquement à partir de la KB, donc ajouter les JSON suffit.

### AST — Assets

#### AST-01 — Captures d'écran OEM × version
- **État** : À faire — 0 %
- **Portée** : Pixel (Android 13/14/15) + Samsung One UI (Android 13/14/15), pour chaque check.

## Résumé

- **Total** : 27 tickets.
- **À faire** : 4.
- **Bloqué** : 0.
- **En cours** : 0.
- **Fait** : 23 (KB-01 à KB-09, AND-01 à AND-11, AND-16, WEB-01, WEB-02).

Décisions prises : package = `app.sentinelle`, KB 100 % embarquée (pas de fetch réseau), profil samsung-oneui = Samsung Account, Bixby, Customization Service, Samsung Push Service, Game Optimizing Service, diagnostics Samsung. INF-01 supprimé (plus de CDN nécessaire), AND-06 (refresh distant) supprimé.
