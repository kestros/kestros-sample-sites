package io.kestros.samples.acpl.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import java.util.ArrayList;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Rule;
import org.junit.Test;

/**
 * Card layouts read their image root from the card ({@code properties.assetRoot}), so the one rule
 * in {@code AcplAssetPaths.root} decides it for every layout, with no Java class in the HTL (#1623).
 */
public class CardAssetRootTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseSlingModelDataSource dataSourceAt(final String path) {
    final Resource resource = context.create().resource(path);
    final BaseSlingModelDataSource source = mock(BaseSlingModelDataSource.class);
    when(source.getResourceResolver()).thenReturn(context.resourceResolver());
    when(source.getResource()).thenReturn(resource);
    when(source.getUiFramework()).thenReturn(mock(UiFramework.class));
    when(source.getLayout(anyString())).thenReturn("default");
    when(source.getElementVariations(anyString(), anyString())).thenReturn(new ArrayList<>());
    when(source.getComponentVariationRetrievalService()).thenReturn(
        mock(ComponentVariationRetrievalService.class));
    when(source.getComponentUiFrameworkViewRetrievalService()).thenReturn(
        mock(ComponentUiFrameworkViewRetrievalService.class));
    return source;
  }

  private SyntheticMatchCard matchCardAt(final String path) throws Exception {
    return new SyntheticMatchCard("result-row", "/content/sites/acpl", "/m.html", "harborside",
        "HAR", "Harborside", "oakvale", "OAK", "Oakvale", "2-1", dataSourceAt(path), "card",
        "match-0");
  }

  private SyntheticFixtureCard fixtureCardAt(final String path) throws Exception {
    return new SyntheticFixtureCard("oakvale", "Oakvale", "Sat", "/content/sites/acpl", "/m.html",
        dataSourceAt(path), "card", "next");
  }

  @Test
  public void testAnAuthoredCardReadsTheAssetLibrary() throws Exception {
    assertEquals("/content/assets/acpl",
        matchCardAt("/content/sites/acpl-authored-p5/jcr:content/main/results").getAssetRoot());
    assertEquals("/content/assets/acpl",
        fixtureCardAt("/content/sites/acpl-authored-p5/jcr:content/main/next").getAssetRoot());
  }

  @Test
  public void testAPublishedCardReadsThePublishedCopy() throws Exception {
    assertEquals("/public/assets/acpl",
        matchCardAt("/public/sites/acpl-authored-p5/jcr:content/main/results").getAssetRoot());
    assertEquals("/public/assets/acpl",
        fixtureCardAt("/public/sites/acpl-authored-p5/jcr:content/main/next").getAssetRoot());
  }
}
