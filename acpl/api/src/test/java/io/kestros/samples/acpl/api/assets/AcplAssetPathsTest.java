package io.kestros.samples.acpl.api.assets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AcplAssetPathsTest {

  @Test
  public void testRootForAuthoredPage() {
    assertEquals("/content/assets/acpl",
        AcplAssetPaths.root("/content/sites/acpl-authored-p5/jcr:content/main/hero"));
  }

  @Test
  public void testRootForPublishedPage() {
    assertEquals("/public/assets/acpl",
        AcplAssetPaths.root("/public/sites/acpl-authored-p5/jcr:content/main/hero"));
  }

  @Test
  public void testRootForPublishedPageRequestUri() {
    assertEquals("/public/assets/acpl", AcplAssetPaths.root("/public/sites/acpl-authored-p5.html"));
  }

  @Test
  public void testRootForNullPath() {
    assertEquals("/content/assets/acpl", AcplAssetPaths.root(null));
  }

  @Test
  public void testRootForPathMerelyContainingPublic() {
    assertEquals("/content/assets/acpl", AcplAssetPaths.root("/content/sites/public/x"));
  }
}
