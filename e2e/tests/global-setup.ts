import { test as setup, expect } from '@playwright/test';

const authFile = '.auth/admin.json';

/**
 * Global setup — authenticates as admin and saves browser state.
 *
 * Sling supports both form-based login and HTTP Basic Auth.
 * We use the Sling form login endpoint to get a session cookie,
 * then save the authenticated state for all test projects to reuse.
 */
setup('authenticate as admin', async ({ page, baseURL }) => {
  const user = process.env.KESTROS_CMS_USER || 'admin';
  const pass = process.env.KESTROS_CMS_PASSWORD || '';

  if (!pass) {
    throw new Error(
      'KESTROS_CMS_PASSWORD environment variable is required. ' +
      'Set it to the admin password for the Kestros CMS dev instance.'
    );
  }

  // Login via Sling form login endpoint
  await page.goto(`${baseURL}/system/sling/form/login`);

  // Fill in the login form
  await page.fill('input[name="j_username"]', user);
  await page.fill('input[name="j_password"]', pass);
  await page.click('button[type="submit"], input[type="submit"]');

  // Wait for redirect after login — Sling redirects to the resource or root
  await page.waitForURL((url) => !url.pathname.includes('/login'), {
    timeout: 15_000,
  });

  // Verify we are authenticated by checking we can access the system console
  const response = await page.goto(`${baseURL}/system/console/bundles`);
  expect(response?.status()).toBeLessThan(400);

  // Save authenticated state
  await page.context().storageState({ path: authFile });
});
