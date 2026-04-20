export type Locale = 'fr' | 'en' | 'es' | 'de';

export const LOCALES: Locale[] = ['fr', 'en', 'es', 'de'];

export const UI = {
  fr: {
    siteTitle: 'Sentinelle',
    tagline: 'Reprenez le contrôle sur votre Android',
    introHome:
      "Sentinelle vous guide, pas à pas, à travers les réglages de votre smartphone pour limiter le pistage et protéger vos données. Aucune donnée n'est collectée.",
    chooseDevice: 'Choisissez votre appareil',
    chooseCountry: 'Choisissez votre pays pour les options locales',
    country: 'Pays',
    severityCritical: 'Critique',
    severityImportant: 'Important',
    severityRecommended: 'Recommandé',
    whyItMatters: 'Pourquoi c\u2019est important',
    steps: 'Étapes',
    openSettings: 'Ouvrir dans les Réglages',
    fallbackPath: 'Chemin manuel',
    dnsSovereign: 'Serveurs DNS souverains',
    jurisdiction: 'Juridiction',
    operator: 'Opérateur',
    nonProfit: 'Association à but non lucratif',
    filtersMalware: 'Filtre les malwares',
    logsNone: 'Aucun log conservé',
    footerPrivacy:
      'Sentinelle est 100% statique et ne collecte aucune donnée. Pas de cookies, pas d\u2019analytics, pas de pisteurs.',
    languageSwitcher: 'Langue',
    backHome: 'Retour à l\u2019accueil',
  },
  en: {
    siteTitle: 'Sentinelle',
    tagline: 'Take back control of your Android',
    introHome:
      "Sentinelle walks you through your phone's settings, step by step, to reduce tracking and protect your data. No data is ever collected.",
    chooseDevice: 'Choose your device',
    chooseCountry: 'Choose your country for local options',
    country: 'Country',
    severityCritical: 'Critical',
    severityImportant: 'Important',
    severityRecommended: 'Recommended',
    whyItMatters: 'Why it matters',
    steps: 'Steps',
    openSettings: 'Open in Settings',
    fallbackPath: 'Manual path',
    dnsSovereign: 'Sovereign DNS servers',
    jurisdiction: 'Jurisdiction',
    operator: 'Operator',
    nonProfit: 'Non-profit organisation',
    filtersMalware: 'Filters malware',
    logsNone: 'No logs kept',
    footerPrivacy:
      'Sentinelle is 100% static and collects no data. No cookies, no analytics, no trackers.',
    languageSwitcher: 'Language',
    backHome: 'Back to home',
  },
  es: {
    siteTitle: 'Sentinelle',
    tagline: 'Recupere el control de su Android',
    introHome:
      'Sentinelle le guía, paso a paso, a través de los ajustes de su teléfono para reducir el rastreo y proteger sus datos. No se recoge ningún dato.',
    chooseDevice: 'Elija su dispositivo',
    chooseCountry: 'Elija su país para las opciones locales',
    country: 'País',
    severityCritical: 'Crítico',
    severityImportant: 'Importante',
    severityRecommended: 'Recomendado',
    whyItMatters: 'Por qué es importante',
    steps: 'Pasos',
    openSettings: 'Abrir en Ajustes',
    fallbackPath: 'Ruta manual',
    dnsSovereign: 'Servidores DNS soberanos',
    jurisdiction: 'Jurisdicción',
    operator: 'Operador',
    nonProfit: 'Organización sin ánimo de lucro',
    filtersMalware: 'Filtra malware',
    logsNone: 'Sin registros',
    footerPrivacy:
      'Sentinelle es 100% estática y no recoge ningún dato. Sin cookies, sin analíticas, sin rastreadores.',
    languageSwitcher: 'Idioma',
    backHome: 'Volver al inicio',
  },
  de: {
    siteTitle: 'Sentinelle',
    tagline: 'Übernehmen Sie wieder die Kontrolle über Ihr Android',
    introHome:
      'Sentinelle führt Sie Schritt für Schritt durch die Einstellungen Ihres Smartphones, um Tracking zu reduzieren und Ihre Daten zu schützen. Es werden keine Daten erfasst.',
    chooseDevice: 'Wählen Sie Ihr Gerät',
    chooseCountry: 'Wählen Sie Ihr Land für lokale Optionen',
    country: 'Land',
    severityCritical: 'Kritisch',
    severityImportant: 'Wichtig',
    severityRecommended: 'Empfohlen',
    whyItMatters: 'Warum es wichtig ist',
    steps: 'Schritte',
    openSettings: 'In Einstellungen öffnen',
    fallbackPath: 'Manueller Pfad',
    dnsSovereign: 'Souveräne DNS-Server',
    jurisdiction: 'Gerichtsbarkeit',
    operator: 'Betreiber',
    nonProfit: 'Gemeinnützige Organisation',
    filtersMalware: 'Filtert Schadsoftware',
    logsNone: 'Keine Protokolle',
    footerPrivacy:
      'Sentinelle ist zu 100 % statisch und erhebt keine Daten. Keine Cookies, keine Analyse, keine Tracker.',
    languageSwitcher: 'Sprache',
    backHome: 'Zurück zur Startseite',
  },
} as const;

export function t(lang: string): (typeof UI)['fr'] {
  return (UI as Record<string, (typeof UI)['fr']>)[lang] ?? UI.fr;
}
