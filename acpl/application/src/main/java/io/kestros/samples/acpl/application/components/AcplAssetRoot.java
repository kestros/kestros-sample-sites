package io.kestros.samples.acpl.application.components;

import io.kestros.samples.acpl.api.assets.AcplAssetPaths;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

/**
 * The ACPL image root for the page being rendered, for card layouts that build image paths in
 * HTL. The rule itself is {@link AcplAssetPaths#root(String)}, so templates and Java agree on
 * when a render is a published one (#1623).
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class AcplAssetRoot {

  @Self
  private SlingHttpServletRequest request;

  /** The asset library root, or its published copy when the request is under {@code /public/}. */
  @Nonnull
  public String getPath() {
    return AcplAssetPaths.root(request.getRequestURI());
  }
}
