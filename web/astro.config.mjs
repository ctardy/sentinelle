import { defineConfig } from 'astro/config';

export default defineConfig({
  site: 'https://sentinelle.app',
  i18n: {
    defaultLocale: 'fr',
    locales: ['fr', 'en', 'es', 'de'],
    routing: {
      prefixDefaultLocale: true,
    },
  },
  trailingSlash: 'always',
  build: {
    format: 'directory',
  },
});
