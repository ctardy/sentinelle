import { readFileSync, readdirSync, existsSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const KB_ROOT = join(__dirname, '..', '..', '..', 'knowledge-base', 'v1');

export type Intent = {
  action: string;
  packageName?: string;
  className?: string;
  data?: string;
  extras?: Record<string, string>;
};

export type Step = {
  label: string;
  intent?: Intent;
  fallbackPath?: string;
};

export type Check = {
  checkId: string;
  title: string;
  risk: string;
  whyItMatters?: string;
  severity?: 'critical' | 'important' | 'recommended';
  autoDetect?: { kind: string; target: string };
  steps: Step[];
  dnsOptionsCountryFile?: string;
};

export type Category = {
  categoryId: string;
  label: string;
  description?: string;
  checks: Check[];
};

export type Profile = {
  profileId: string;
  language: string;
  label: string;
  manufacturer: string;
  androidVersions: Array<{ minSdk: number; maxSdk: number }>;
  categories: Category[];
};

export type DnsServer = {
  hostname: string;
  operator: string;
  jurisdiction: string;
  nonProfit?: boolean;
  filtersMalware?: boolean;
  logsPolicy?: 'no-logs' | 'minimal' | 'unknown';
  notesKey?: string;
};

export type CountryDns = {
  country: string;
  servers: DnsServer[];
};

export type IndexEntry = {
  profileId: string;
  manufacturer: string;
  languages: string[];
  androidVersions: Array<{ minSdk: number; maxSdk: number }>;
};

export type Index = {
  schemaVersion: string;
  updatedAt: string;
  profiles: IndexEntry[];
  dnsCountries: string[];
  defaultDnsCountry: string;
};

export function loadIndex(): Index {
  return JSON.parse(readFileSync(join(KB_ROOT, 'index.json'), 'utf-8'));
}

export function loadProfile(profileId: string, lang: string): Profile | null {
  const path = join(KB_ROOT, 'profiles', `${profileId}.${lang}.json`);
  if (!existsSync(path)) return null;
  return JSON.parse(readFileSync(path, 'utf-8'));
}

export function loadDns(country: string): CountryDns | null {
  const path = join(KB_ROOT, 'dns', `${country.toLowerCase()}.json`);
  if (!existsSync(path)) return null;
  return JSON.parse(readFileSync(path, 'utf-8'));
}

export function listDnsCountries(): string[] {
  const dir = join(KB_ROOT, 'dns');
  if (!existsSync(dir)) return [];
  return readdirSync(dir)
    .filter((f) => f.endsWith('.json'))
    .map((f) => f.replace('.json', '').toUpperCase());
}
