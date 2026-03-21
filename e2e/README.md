# Kestros CMS E2E Tests

Playwright-based end-to-end tests for the Kestros CMS dev instance.

## Prerequisites

- Node.js 18+
- Access to the Kestros CMS dev instance (default: `http://192.168.86.216:8000`)

## Setup

```bash
cd e2e
npm install
npx playwright install
```

## Configuration

Tests target the Kestros CMS dev instance. Configure via environment variables:

| Variable | Default | Description |
|---|---|---|
| `KESTROS_CMS_URL` | `http://192.168.86.216:8000` | Base URL of the CMS instance |
| `KESTROS_CMS_USER` | `admin` | CMS admin username |
| `KESTROS_CMS_PASSWORD` | *(required)* | CMS admin password |

## Running Tests

```bash
# Set the CMS password (required)
export KESTROS_CMS_PASSWORD=your-password

# Run all tests
npm test

# Run smoke tests only
npm run test:smoke

# Run tests in headed mode (see the browser)
npm run test:headed

# Run tests with Playwright UI
npm run test:ui

# View the HTML report
npm run report
```

## Test Structure

```
e2e/
  playwright.config.ts    # Playwright configuration
  tests/
    global-setup.ts       # Authenticates as admin, saves session state
    global-teardown.ts    # Cleans up test data (placeholder)
    smoke.spec.ts         # Smoke tests — instance reachability & auth
  .auth/                  # Saved auth state (gitignored)
  test-results/           # Test output (gitignored)
  playwright-report/      # HTML report (gitignored)
```

## How It Works

1. **Authentication**: The `global-setup.ts` project logs in via the Sling form login endpoint (`/system/sling/form/login`) and saves the authenticated browser state to `.auth/admin.json`. All test projects reuse this state so they don't need to log in individually.

2. **Test Data**: Tests that create content use the Sling POST servlet directly via Playwright's `request` API. Content is created under unique paths and cleaned up in each test's teardown.

3. **Cross-Browser**: Tests run in Chromium, Firefox, and WebKit by default. Use `--project=chromium` to run in a single browser.

## CI Integration

For Jenkins or other CI systems:

```bash
# Install dependencies
npm ci
npx playwright install --with-deps

# Run tests with JUnit output
KESTROS_CMS_PASSWORD=$CMS_PASSWORD npx playwright test --reporter=junit
```

Test results are written to `test-results/results.xml` in JUnit format when `CI=true` is set.

## Adding New Tests

1. Create a new `.spec.ts` file in `tests/`
2. Tests automatically inherit the authenticated session from `global-setup.ts`
3. Use `test.describe` groups with tags (e.g., `@smoke`, `@dialogs`) for selective runs
4. Clean up any JCR nodes created during tests in an `afterAll` hook
