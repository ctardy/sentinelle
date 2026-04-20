# Prompt de démarrage — Sentinelle

> À copier-coller comme premier message d'une nouvelle session de développement (humaine ou assistée par un agent IA) ouverte sur `C:\dev\projects\sentinel`.

---

Tu interviens sur **Sentinelle**, application Android + site web d'audit de vie privée et de souveraineté numérique. Elle est **autonome, gratuite, sans tracking, sans backend**. Pas de produit d'appel, pas de compte, pas de capture de leads.

## À lire en priorité

1. `AGENTS.md` à la racine — **règles absolues et architecture** (à respecter sans exception).
2. `README.md` — vue publique du projet (en anglais).
3. `README.fr.md` — vue publique en français.
4. `knowledge-base/schema.json` et `schema-dns.json` — format de la base de connaissances.

## Principes non négociables (rappel court)

- **No tracking, no network** : aucune télémétrie, aucun analytics tiers, aucun appel réseau depuis l'APK. C'est l'argument commercial central.
- **No backend dédié** : KB embarquée dans l'APK sous forme de JSON statique. Mise à jour = nouvelle release Play Store.
- **Base de connaissances = source unique** : tous les textes, intents, options d'audit vivent dans `knowledge-base/v1/*.json`. Jamais en dur dans le code.
- **App internationale** : UI en FR (défaut), EN, ES, DE. Options locales (DNS souverains) séparées de la langue, indexées par pays.
- **Communication, commits, docs internes** : en français avec les accents. README public en anglais.
- **Incrémenter `android/version.properties`** avant chaque build APK release (`./gradlew bumpVersionCode` ou `bumpVersionName`).
- **Git prudent** : toujours `git status` + `git diff --cached` avant commit. Jamais `git add .` ni `-A`.
- **Pas de rétro-compatibilité** : on supprime l'ancien code, pas de `@Deprecated`.

## État actuel du projet

### Déjà fait

- `AGENTS.md`, `README.md` (EN), `README.fr.md`, `.gitignore`.
- **Knowledge-base v1** complète :
  - Schémas JSON (`schema.json`, `schema-dns.json`).
  - `index.json` (catalogue des profils et pays).
  - Profils **`android-generic`** et **`samsung-oneui`** en FR / EN / ES / DE.
  - DNS souverains FR, DE, ES, GB, EU.
- **App Android** scaffoldée et fonctionnelle :
  - Kotlin + Jetpack Compose + Material 3, package `app.sentinelle`, aucune permission.
  - Lecteur KB depuis les assets via `kotlinx.serialization`.
  - Onboarding (auto-détection constructeur / version, sélection profil + pays), persistance DataStore.
  - Écran d'audit (liste des checks par catégorie, progression + score).
  - Écran détail de check avec ouverture d'Intents Réglages et DNS recommandés.
  - Script Gradle `syncKnowledgeBase` qui copie la KB dans les assets au build.
  - Strings splittés par domaine, 4 langues.
- **Site web Astro** : 32 pages générées, i18n FR/EN/ES/DE, toutes les pages DNS et profil remplies.

### Pas encore fait

- **Thème Material 3 custom** (AND-13) : palette + dark mode alignés sur le site.
- **Captures d'écran** par OEM × version Android (AST-01) et leur intégration dans l'écran détail (AND-14).
- **Auto-bump de `version.properties`** dans la CI (AND-15 — partiellement fait via les tâches Gradle manuelles).
- **Sélecteur pays inline dans l'audit** pour changer sans repasser par l'onboarding (AND-12).

## Priorités suggérées

1. **Tester l'APK** sur un vrai appareil, itérer l'UX.
2. **AST-01** : captures d'écran Pixel / Samsung Galaxy, les intégrer à l'écran détail.
3. **AND-13** : thème Material 3 cohérent avec le site.
4. **AND-12** : sélecteur pays inline.

## Commandes clés

```bash
# Site web (dev)
cd /c/dev/projects/sentinel/web && npm run dev

# Site web (build)
cd /c/dev/projects/sentinel/web && npm run build

# Android (debug APK)
cd /c/dev/projects/sentinel/android
JAVA_HOME="C:\dev\softs\java\jdk-21.0.3+9" cmd.exe //c ".\gradlew.bat assembleDebug"
# Toujours arrêter le daemon après :
cd /c/dev/projects/sentinel/android && ./gradlew --stop
```

## Structure du dépôt

```
sentinel/
├── AGENTS.md                    # Règles projet (lecture obligatoire)
├── README.md                    # Vue publique EN
├── README.fr.md                 # Vue publique FR
├── android/                     # App Kotlin + Compose
├── web/                         # Site Astro
├── knowledge-base/
│   ├── schema.json
│   ├── schema-dns.json
│   └── v1/
│       ├── index.json
│       ├── dns/                 # {country}.json
│       └── profiles/            # {profileId}.{lang}.json
└── docs/
    ├── tickets.md               # Backlog
    └── prompt-demarrage.md      # Ce fichier
```

## Cadre de travail attendu

- Commence toujours par lire `AGENTS.md` et prendre connaissance de l'état réel avant de proposer quoi que ce soit.
- Pour toute décision structurante (nom de package, choix de lib, refonte d'un schéma), **propose d'abord, attends validation** avant d'implémenter.
- Ne commite **rien** sans demande explicite.
