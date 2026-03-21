import { test, expect } from '@playwright/test';

/**
 * Smoke tests for Kestros CMS dev instance.
 *
 * These tests verify:
 * 1. The dev instance is reachable
 * 2. Authentication works (via storageState from global setup)
 * 3. Core CMS endpoints respond correctly
 *
 * All tests are tagged @smoke so they can be run independently:
 *   npx playwright test --grep @smoke
 */
test.describe('Kestros CMS Smoke Tests @smoke', () => {

  test('dev instance is reachable', async ({ page }) => {
    const response = await page.goto('/');
    expect(response).not.toBeNull();
    expect(response!.status()).toBeLessThan(500);
  });

  test('admin is authenticated — Felix console accessible', async ({ page }) => {
    const response = await page.goto('/system/console/bundles');
    expect(response).not.toBeNull();
    expect(response!.status()).toBe(200);
    // The Felix console page should contain bundle information
    await expect(page.locator('body')).toContainText('Bundle');
  });

  test('Sling health check responds', async ({ request }) => {
    const response = await request.get('/system/health', {
      // Playwright treats non-2xx as failures by default for request API;
      // we explicitly allow any status since 503 (unhealthy) is a valid response
      failOnStatusCode: false,
    });
    // Sling health check returns 200 (healthy), 503 (unhealthy), or 404 (not configured).
    // Any of these confirm the instance is running and responding.
    expect([200, 404, 503]).toContain(response.status());
  });

  test('content root is accessible', async ({ request }) => {
    const response = await request.get('/content.json');
    expect(response.status()).toBe(200);
    const body = await response.json();
    // The content root should have at least a jcr:primaryType
    expect(body).toHaveProperty('jcr:primaryType');
  });

  test('Sling POST servlet is available — can create and delete test node', async ({ request, baseURL }) => {
    const nodeName = 'e2e-smoke-test-' + Date.now();
    const testPath = '/content/' + nodeName;

    // Create a test node via Sling POST servlet.
    // POST to the parent (/content/) with :name parameter — Kestros CMS has a
    // LockEnforcementServletFilter that throws 500 when POSTing directly to a
    // non-existent path, so we must use the :name approach instead.
    const createResponse = await request.post(`${baseURL}/content/`, {
      form: {
        ':name': nodeName,
        'jcr:primaryType': 'nt:unstructured',
        'testProperty': 'e2e-smoke-test',
      },
    });
    expect(createResponse.status()).toBeLessThan(400);

    // Verify the node was created
    const getResponse = await request.get(`${baseURL}${testPath}.json`);
    expect(getResponse.status()).toBe(200);
    const node = await getResponse.json();
    expect(node.testProperty).toBe('e2e-smoke-test');

    // Clean up — delete the test node
    const deleteResponse = await request.post(`${baseURL}${testPath}`, {
      form: { ':operation': 'delete' },
    });
    expect(deleteResponse.status()).toBeLessThan(400);
  });

  test('login page renders', async ({ browser }) => {
    // Use a fresh context without auth to test the login page
    const context = await browser.newContext();
    const page = await context.newPage();
    const baseURL = process.env.KESTROS_CMS_URL || 'http://192.168.86.216:8000';

    const response = await page.goto(`${baseURL}/system/sling/form/login`);
    expect(response).not.toBeNull();
    expect(response!.status()).toBe(200);

    // Login page should have username and password fields
    await expect(page.locator('input[name="j_username"]')).toBeVisible();
    await expect(page.locator('input[name="j_password"]')).toBeVisible();

    await context.close();
  });
});
