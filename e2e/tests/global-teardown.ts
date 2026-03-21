import { test as teardown } from '@playwright/test';

/**
 * Global teardown — cleans up any test data created during the test run.
 *
 * Currently a no-op. As tests are added that create content in JCR,
 * this file should be updated to delete test content nodes
 * (e.g., /content/e2e-tests/*) via Sling POST servlet.
 */
teardown('cleanup test data', async ({ request, baseURL }) => {
  // Future: Delete test content nodes created during the test run.
  // Example:
  // await request.post(`${baseURL}/content/e2e-tests`, {
  //   form: { ':operation': 'delete' },
  // });
});
