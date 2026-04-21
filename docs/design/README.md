# Propositions d'icône Sentinelle

Quatre concepts en SVG, tous alignés sur le gabarit Android adaptive icon (`viewBox="0 0 108 108"`, zone visible ≈ 72×72 centrée).

Aperçu rapide : ouvre chaque fichier dans un navigateur (double-clic).

| Fichier | Concept | Symbolique | Palette |
| --- | --- | --- | --- |
| `icon-a-shield.svg` | Bouclier blanc + coche bleue sur fond bleu | Classique, *« vos réglages sont validés et protégés »* | `#2F6FEB` + blanc |
| `icon-b-beacon.svg` | Phare / tour de guet avec faisceaux | Référence directe au nom **Sentinelle**, veille active | `#0F172A` + `#F59E0B` |
| `icon-c-radar-s.svg` | Lettre **S** au centre de cercles de radar | Typographique, moderne, évoque l'audit | `#2F6FEB` + blanc |
| `icon-d-closed-eye.svg` | Œil fermé + cadenas | Anti-pistage : *« ce qui vous observait est verrouillé »* | `#0F172A` + blanc + `#F59E0B` |

## Conventions respectées

- **viewBox 0 0 108 108** : taille standard d'un adaptive icon Android.
- **Zone safe centrale 18 → 90 (72 dp)** : tous les éléments significatifs tiennent dedans, le fond occupe les 108 dp complets (Android recadre en cercle ou rond-carré selon le lanceur).
- **2 à 3 couleurs** chacune, aucun dégradé complexe, bonne lecture en 48 × 48 dp (notifications, liste d'apps).

## Prochaine étape

Quand tu auras choisi, je convertis le SVG retenu en deux `VectorDrawable` XML (`ic_launcher_background.xml` + `ic_launcher_foreground.xml`) et je les câble dans `mipmap-anydpi-v26/ic_launcher.xml` + `AndroidManifest.xml`. Les deux autres versions (notification + monochrome thémable Android 13+) se déclinent à partir de la même source.
