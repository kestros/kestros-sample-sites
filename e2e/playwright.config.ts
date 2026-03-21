import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright configuration for Kestros CMS E2E tests.
 *
 * Tests run against the Kestros CMS dev instance at port 8000.
 * Auth credentials are read from environment variables.
 *
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './tests',
  /* Run tests sequentially in CI, parallel locally */
  fullyParallel: true,
  /* Fail the build on CI if you accidentally left test.only in the source code */
  forbidOnly: !!process.env.CI,
  /* Retry on CI only */
  retries: process.env.CI ? 2 : 0,
  /* Limit parallel workers on CI */
  workers: process.env.CI ? 1 : undefined,
  /* Reporter configuration */
  reporter: process.env.CI
    ? [['junit', { outputFile: 'test-results/results.xml' }], ['html', { open: 'never' }]]
    : [['html', { open: 'on-failure' }]],
  /* Shared settings for all the projects below */
  use: {
    /* Base URL for the Kestros CMS dev instance */
    baseURL: process.env.KESTROS_CMS_URL || 'http://192.168.86.216:8000',

    /* HTTP credentials for Sling Basic Auth */
    httpCredentials: {
      username: process.env.KESTROS_CMS_USER || 'admin',
      password: process.env.KESTROS_CMS_PASSWORD || '',
    },

    /* Collect trace on first retry */
    trace: 'on-first-retry',

    /* Screenshot on failure */
    screenshot: 'only-on-failure',

    /* Video on failure */
    video: 'on-first-retry',
  },

  /* Configure projects for major browsers */
  projects: [
    /* Setup project — authenticates and saves state */
    {
      name: 'setup',
      testMatch: /global-setup\.ts/,
      teardown: 'teardown',
    },
    {
      name: 'teardown',
      testMatch: /global-teardown\.ts/,
    },
    {
      name: 'chromium',
      use: {
        ...devices['Desktop Chrome'],
        /* Use authenticated state from setup */
        storageState: '.auth/admin.json',
      },
      dependencies: ['setup'],
    },
    {
      name: 'firefox',
      use: {
        ...devices['Desktop Firefox'],
        storageState: '.auth/admin.json',
      },
      dependencies: ['setup'],
    },
    {
      name: 'webkit',
      use: {
        ...devices['Desktop Safari'],
        storageState: '.auth/admin.json',
      },
      dependencies: ['setup'],
    },
  ],

  /* Timeout for each test */
  timeout: 30_000,

  /* Timeout for each expect() assertion */
  expect: {
    timeout: 10_000,
  },
});
