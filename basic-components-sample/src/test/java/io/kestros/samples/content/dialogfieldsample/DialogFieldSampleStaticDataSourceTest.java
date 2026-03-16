package io.kestros.samples.content.dialogfieldsample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.sitebuilding.api.services.KestrosClassLoader;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for DialogFieldSampleStaticDataSource.
 */
public class DialogFieldSampleStaticDataSourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private DialogFieldSampleStaticDataSource dataSource;
  private Map<String, Object> properties;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    ComponentUiFrameworkViewRetrievalService viewRetrievalService =
        mock(ComponentUiFrameworkViewRetrievalService.class);
    ComponentVariationRetrievalService variationRetrievalService =
        mock(ComponentVariationRetrievalService.class);
    KestrosClassLoader classLoader = mock(KestrosClassLoader.class);
    ThemeProviderService themeProviderService = mock(ThemeProviderService.class);
    UiFrameworkRetrievalService uiFrameworkRetrievalService =
        mock(UiFrameworkRetrievalService.class);
    ThemeRetrievalService themeRetrievalService = mock(ThemeRetrievalService.class);

    Theme theme = mock(Theme.class);
    UiFramework uiFramework = mock(UiFramework.class);
    when(theme.getUiFramework()).thenReturn(uiFramework);
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);
    when(themeProviderService.getThemeForComponent(any())).thenReturn(theme);

    context.registerService(ComponentUiFrameworkViewRetrievalService.class, viewRetrievalService);
    context.registerService(ComponentVariationRetrievalService.class, variationRetrievalService);
    context.registerService(KestrosClassLoader.class, classLoader);
    context.registerService(ThemeProviderService.class, themeProviderService);
    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);
    context.registerService(ThemeRetrievalService.class, themeRetrievalService);

    properties = new HashMap<>();
    properties.put("sling:resourceType", KestrosDialogFieldSample.RESOURCE_TYPE);
  }

  private DialogFieldSampleStaticDataSource adaptResource(Map<String, Object> props) {
    Resource resource = context.create().resource("/content/dialog-field-sample", props);
    context.request().setResource(resource);
    return context.request().adaptTo(DialogFieldSampleStaticDataSource.class);
  }

  @Test
  public void testGetSampleText() {
    properties.put("sampleText", "Hello World");
    dataSource = adaptResource(properties);
    assertNotNull(dataSource);
    assertEquals("Hello World", dataSource.getSampleText());
  }

  @Test
  public void testGetSampleTextWhenNull() {
    dataSource = adaptResource(properties);
    assertNotNull(dataSource);
    assertNull(dataSource.getSampleText());
  }

  @Test
  public void testGetSampleTextarea() {
    properties.put("sampleTextarea", "Multi-line text");
    dataSource = adaptResource(properties);
    assertEquals("Multi-line text", dataSource.getSampleTextarea());
  }

  @Test
  public void testGetSampleRichtext() {
    properties.put("sampleRichtext", "<p>Rich <strong>text</strong></p>");
    dataSource = adaptResource(properties);
    assertEquals("<p>Rich <strong>text</strong></p>", dataSource.getSampleRichtext());
  }

  @Test
  public void testGetSampleCheckboxTrue() {
    properties.put("sampleCheckbox", true);
    dataSource = adaptResource(properties);
    assertTrue(dataSource.getSampleCheckbox());
  }

  @Test
  public void testGetSampleCheckboxFalse() {
    properties.put("sampleCheckbox", false);
    dataSource = adaptResource(properties);
    assertFalse(dataSource.getSampleCheckbox());
  }

  @Test
  public void testGetSamplePath() {
    properties.put("samplePath", "/content/sites/test");
    dataSource = adaptResource(properties);
    assertEquals("/content/sites/test", dataSource.getSamplePath());
  }

  @Test
  public void testGetSampleSelect() {
    properties.put("sampleSelect", "optionB");
    dataSource = adaptResource(properties);
    assertEquals("optionB", dataSource.getSampleSelect());
  }

  @Test
  public void testGetSampleMultifieldEmpty() {
    dataSource = adaptResource(properties);
    assertNotNull(dataSource);
    List<String> result = dataSource.getSampleMultifield();
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testGetSampleMultifieldWithItems() {
    Resource resource = context.create().resource("/content/dialog-field-sample-multi", properties);
    Map<String, Object> item0Props = new HashMap<>();
    item0Props.put("item", "First Item");
    context.create().resource("/content/dialog-field-sample-multi/sampleMultifield/item0", item0Props);
    Map<String, Object> item1Props = new HashMap<>();
    item1Props.put("item", "Second Item");
    context.create().resource("/content/dialog-field-sample-multi/sampleMultifield/item1", item1Props);
    context.request().setResource(resource);
    dataSource = context.request().adaptTo(DialogFieldSampleStaticDataSource.class);
    assertNotNull(dataSource);
    List<String> result = dataSource.getSampleMultifield();
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("First Item", result.get(0));
    assertEquals("Second Item", result.get(1));
  }

  @Test
  public void testGetSampleTag() {
    properties.put("sampleTag", "sample-tag");
    dataSource = adaptResource(properties);
    assertEquals("sample-tag", dataSource.getSampleTag());
  }

  @Test
  public void testGetSampleNumber() {
    properties.put("sampleNumber", "42");
    dataSource = adaptResource(properties);
    assertEquals("42", dataSource.getSampleNumber());
  }

  @Test
  public void testGetSampleDate() {
    properties.put("sampleDate", "2026-03-06");
    dataSource = adaptResource(properties);
    assertEquals("2026-03-06", dataSource.getSampleDate());
  }

  @Test
  public void testGetSampleDatetime() {
    properties.put("sampleDatetime", "2026-03-06T14:30:00");
    dataSource = adaptResource(properties);
    assertEquals("2026-03-06T14:30:00", dataSource.getSampleDatetime());
  }

  @Test
  public void testGetSampleToggleTrue() {
    properties.put("sampleToggle", true);
    dataSource = adaptResource(properties);
    assertTrue(dataSource.getSampleToggle());
  }

  @Test
  public void testGetSampleRadio() {
    properties.put("sampleRadio", "radio2");
    dataSource = adaptResource(properties);
    assertEquals("radio2", dataSource.getSampleRadio());
  }

  @Test
  public void testGetSampleHidden() {
    properties.put("sampleHidden", "hidden-value-123");
    dataSource = adaptResource(properties);
    assertEquals("hidden-value-123", dataSource.getSampleHidden());
  }

  @Test
  public void testGetSampleImage() {
    properties.put("sampleImage", "/content/assets/sample-image.png");
    dataSource = adaptResource(properties);
    assertEquals("/content/assets/sample-image.png", dataSource.getSampleImage());
  }

  @Test
  public void testAllFieldsPopulated() {
    properties.put("sampleText", "text value");
    properties.put("sampleTextarea", "textarea value");
    properties.put("sampleRichtext", "<p>rich text</p>");
    properties.put("sampleCheckbox", true);
    properties.put("samplePath", "/content/test");
    properties.put("sampleSelect", "optionA");
    properties.put("sampleTag", "tag-value");
    properties.put("sampleNumber", "99");
    properties.put("sampleDate", "2026-01-01");
    properties.put("sampleDatetime", "2026-01-01T12:00:00");
    properties.put("sampleToggle", false);
    properties.put("sampleRadio", "radio1");
    properties.put("sampleHidden", "secret");
    properties.put("sampleImage", "/content/assets/photo.jpg");

    dataSource = adaptResource(properties);
    assertNotNull(dataSource);
    assertEquals("text value", dataSource.getSampleText());
    assertEquals("textarea value", dataSource.getSampleTextarea());
    assertEquals("<p>rich text</p>", dataSource.getSampleRichtext());
    assertTrue(dataSource.getSampleCheckbox());
    assertEquals("/content/test", dataSource.getSamplePath());
    assertEquals("optionA", dataSource.getSampleSelect());
    assertNotNull(dataSource.getSampleMultifield());
    assertEquals("tag-value", dataSource.getSampleTag());
    assertEquals("99", dataSource.getSampleNumber());
    assertEquals("2026-01-01", dataSource.getSampleDate());
    assertEquals("2026-01-01T12:00:00", dataSource.getSampleDatetime());
    assertFalse(dataSource.getSampleToggle());
    assertEquals("radio1", dataSource.getSampleRadio());
    assertEquals("secret", dataSource.getSampleHidden());
    assertEquals("/content/assets/photo.jpg", dataSource.getSampleImage());
  }
}
