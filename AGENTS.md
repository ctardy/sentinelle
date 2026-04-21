# AGENTS.md — Règles et conventions projet

> Fichier d'instructions lu automatiquement par les agents de développement (humains et IA) qui travaillent sur ce dépôt. Conserve les contraintes produit, de stack et d'hygiène de dépôt.

## Règles absolues (priorité maximale)

- **Langue de travail** : communication, commentaires de code, documentation interne et commits toujours en français avec les accents (é, è, ê, à, ù, ç, etc.)
- **Langues supportées par l'app** : **application internationale** — **EN par défaut**, FR, ES, DE, extensible. Aucune chaîne visible par l'utilisateur en dur dans le code : tout passe par les ressources localisées Android (`res/values/` = EN, `res/values-fr/`, `res/values-es/`, `res/values-de/`) et par les fichiers JSON localisés de la KB. Fallback code quand la locale système n'est pas disponible : `en`. Découper `strings_*.xml` par domaine (`strings.xml`, `strings_audit.xml`, `strings_wizard.xml`, `strings_a11y.xml`, etc.)
- **No tracking, no network** : argument commercial central. Aucune télémétrie, aucun analytics tiers (pas de Firebase Analytics, pas de Crashlytics sans opt-in explicite, pas de Google Play Services optionnels). **L'APK ne fait aucun appel réseau** — la base de connaissances est embarquée en assets, mise à jour uniquement via les releases Play Store
- **No backend dédié** : Sentinelle est une APK autonome. Base de connaissances = JSON statique versionné, embarqué dans l'APK. Pas d'API propre, pas de base de données serveur, pas de compte utilisateur, pas de fetch distant
- **Base de connaissances** : tous les points d'audit, libellés, descriptions, deep links viennent de `knowledge-base/v{N}/*.json` — jamais en dur dans le code Kotlin. Les fichiers sont copiés dans `android/app/src/main/assets/` au build
- **KB localisée** : un fichier par profil × langue, ex: `knowledge-base/v1/android-generic.fr.json`, `android-generic.en.json`, etc. L'app choisit automatiquement selon la locale système, avec fallback en anglais puis en français. La structure (ordre des catégories, IDs, Intents) est identique entre langues ; seuls les libellés et explications changent
- **Options locales** (DNS souverains, autorités de protection des données, etc.) : paramétrées par pays via un champ `country` dans la KB, pas via la langue. Un utilisateur allemand parlant anglais doit voir les DNS allemands
- **No hardcode** : aucun libellé OEM, aucun texte d'audit, aucun intent Android en dur dans le code. Tout passe par la KB
- **No CDN externe** : toutes les libs JS/CSS éventuelles (si webview d'aide) servies localement. Jamais de jsdelivr/cdnjs/unpkg/Google Fonts
- **Chemins shell** : toujours des chemins absolus (ex: `/c/dev/projects/sentinel/android`, jamais `./android`)
- **Git pull avec changements en cours** : `git stash && git pull --rebase && git stash pop`
- **Messaging** : "audit vie privée", "souveraineté numérique", "reprenez le contrôle". Jamais "MVP", ne pas mettre Google en avant. Sentinelle est une application **autonome, gratuite, sans collecte**
- **No alert/confirm/prompt côté WebView** : si WebView d'aide, jamais de `window.alert/confirm/prompt`
- **Pas de migration Room** : la BDD locale Android est jetable. Modifier les entités directement, pas de `MIGRATION_X_Y`
- **Messages d'erreur utilisateur** : jamais de message technique (codes HTTP, URLs, stack traces, noms d'exception). Toujours des messages rassurants orientés solution
- **Arrêt Gradle après build** : après `assembleDebug/assembleRelease`, toujours `gradlew --stop` depuis `android/`. Jamais `taskkill`
- **⛔ No rétro-compatibilité** : pas de clients en production, pas de `@Deprecated`, pas de fallback "au cas où". Quand un flux est remplacé, on supprime l'ancien
- **Git commit prudent** : vérifier `git status` et `git diff --cached` avant commit, ne stager que les fichiers liés à la tâche. Jamais `git add .` ni `-A`. Jamais `reset --hard` ni `checkout .` sans confirmation explicite
- **Incrémenter `version.properties` avant chaque build release**
- **Mot "Français"** : toujours avec un F majuscule dans le code
- **Pas de sous-titres descriptifs** dans les ListItem de navigation Android

---

## Description du projet

**Sentinelle** — Application Android d'audit de vie privée et de souveraineté numérique.

**Positionnement** : application **autonome**, **gratuite**, **sans tracking**, **sans backend**. Ni produit d'appel, ni passerelle commerciale. L'utilisateur télécharge, audite, repart avec un smartphone mieux maîtrisé.

### Promesse utilisateur

L'utilisateur sélectionne son constructeur et sa version d'Android. Sentinelle :
1. Détecte ce qui est détectable sans permissions invasives (`Build.MANUFACTURER`, `Build.MODEL`, `Build.VERSION.SDK_INT`, présence de packages OEM, statut Advertising ID).
2. Propose un audit guidé pas-à-pas : pour chaque point (ID publicitaire, DNS souverain, assistant vocal, diagnostics OEM, permissions apps…), explique le risque, ouvre directement le bon écran des Réglages Android via Intent, et laisse l'utilisateur cocher "fait".
3. Affiche un score de souveraineté en sortie.

### Périmètre MVP

- **Constructeurs** : Android stock (AOSP / Pixel) + Samsung One UI (≈80 % du marché mondial combiné avec la suite)
- **Android** : versions 10 à 15
- **Audits** : ~15 à 20 points
- **Langues UI** : FR, EN, ES, DE
- **Pays couverts (options locales : DNS, autorités CNIL-like)** : France, Allemagne, Espagne, Royaume-Uni + "Europe" et "International" comme profils génériques. Extensible
- Pas de VPN, pas de blocage de trackers actif, pas de root requis. Uniquement de l'assistance guidée

### Ce que Sentinelle n'est PAS

- Pas un firewall (cf. NetGuard, TrackerControl)
- Pas un scanner de trackers dans APK (cf. Exodus Privacy)
- Pas un VPN
- Pas un produit payant
- Pas un produit d'appel pour un autre service

---

## Architecture

### APK Android autonome

- **Langage** : Kotlin
- **Package** : `app.sentinelle`
- **UI** : Jetpack Compose + Material 3
- **minSdk** : 29 (Android 10) — cohérent avec le périmètre d'audit
- **targetSdk** : dernière stable
- **Stockage local** : Room (états d'avancement de l'audit, préférences utilisateur)
- **Réseau** : aucun. Pas d'OkHttp, pas de permission `INTERNET`

### Base de connaissances (source de vérité)

```
knowledge-base/
├── schema.json              # JSON Schema du format profil
├── schema-dns.json          # JSON Schema des options DNS par pays
├── v1/
│   ├── index.json           # Liste des profils et langues disponibles
│   ├── dns/
│   │   ├── fr.json          # DNS souverains pour la France
│   │   ├── de.json
│   │   └── ...
│   ├── profiles/
│   │   ├── android-generic.fr.json
│   │   ├── android-generic.en.json
│   │   ├── android-generic.es.json
│   │   ├── android-generic.de.json
│   │   ├── samsung-oneui.fr.json
│   │   └── ...
```

- Les fichiers `knowledge-base/v{N}/` sont copiés dans `android/app/src/main/assets/kb/` à chaque build (script Gradle)
- Versionnage : le dossier `v{N}` bump quand une rupture de schéma est nécessaire
- Mise à jour des checks = nouvelle release APK sur le Play Store

---

## Commandes de build

```bash
# Android
cd /c/dev/projects/sentinel/android
JAVA_HOME="C:\dev\softs\java\jdk-21.0.3+9" cmd.exe //c ".\gradlew.bat assembleDebug"
# Toujours arrêter le daemon après :
cd /c/dev/projects/sentinel/android && ./gradlew --stop
```
