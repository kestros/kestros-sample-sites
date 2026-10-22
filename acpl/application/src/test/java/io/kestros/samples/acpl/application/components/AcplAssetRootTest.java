package io.kestros.samples.acpl.application.components;

import static org.junit.Assert.assertEquals;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AcplAssetRootTest {

  @Rule
  public SlingContext context = new SlingContext();

  @Before
  public void setUp() {
    context.addModelsForClasses(AcplAssetRoot.class);
  }

  private String rootFor(final String uri) {
    ((MockRequestPathInfo) context.request().getRequestPathInfo()).setResourcePath(uri);
    context.request().setPathInfo(uri);
    return context.request().adaptTo(AcplAssetRoot.class).getPath();
  }

  @Test
  public void testAnAuthoredPageReadsTheAssetLibrary() {
    assertEquals("/content/assets/acpl", rootFor("/content/sites/acpl-authored-p5.html"));
  }

  @Test
  public void testAPublishedPageReadsThePublishedCopy() {
    assertEquals("/public/assets/acpl", rootFor("/public/sites/acpl-authored-p5.html"));
  }

  @Test
  public void testPublicInsideAPathIsNotAPublishedRender() {
    assertEquals("/content/assets/acpl", rootFor("/content/sites/acpl/public/news.html"));
  }
}
