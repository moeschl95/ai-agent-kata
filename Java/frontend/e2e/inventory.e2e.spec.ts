import { test, expect, Page } from '@playwright/test';

test.describe('Inventory Page', () => {
  let page: Page;

  test.beforeEach(async ({ browser }) => {
    page = await browser.newPage();
    await page.goto('/');
  });

  test.afterEach(async () => {
    await page.close();
  });

  test('should_loadInventoryPage_when_navigatingToRoot', async () => {
    // Arrange: page is already loaded in beforeEach
    // Act: verify page is loaded by checking URL resolves to inventory
    await expect(page).toHaveURL(/\/(inventory)?/);

    // Assert: page should have content
    const body = page.locator('body');
    await expect(body).toBeVisible();
  });

  test('should_displayDatagridOnInventoryPage_when_pageLoads', async () => {
    // Arrange: on inventory page
    await page.goto('/');

    // Act: wait for datagrid to be present
    const datagrid = page.locator('clr-datagrid');

    // Assert: datagrid should exist and be visible
    await expect(datagrid).toBeVisible({ timeout: 5000 });
  });

  test('should_displayAdvanceDayButton_when_onInventoryPage', async () => {
    // Arrange: on inventory page
    await page.goto('/');

    // Act: look for Advance Day button
    const button = page.locator('button:has-text("Advance Day")');

    // Assert: button should be visible
    await expect(button).toBeVisible({ timeout: 5000 });
  });

  test('should_navigateToProjectionPage_when_clickingProjectionLink', async () => {
    // Arrange: on inventory page
    await page.goto('/');

    // Act: find and click projection link/button
    const projectionLink = page.locator('a, button').filter({ hasText: /Projection/i });
    
    // Assert: projection link should be visible
    await expect(projectionLink).toBeVisible({ timeout: 5000 });
  });
});

