package io.kestros.samples.acpl.application.components;

import io.kestros.samples.acpl.api.assets.AcplAssetPaths;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

/**
 * The ACPL image root for a template: {@code ${acplAssets.root}/crests/<slug>.svg}.
 *
 * <p>Read from the request URI, not the resource path, because the templates that draw crests and
 * portraits mostly render datasource rows whose resources are synthetic; the URI is the page being
 * rendered, under {@code /public} when it is the published copy (#1623).
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class AcplAssets {

  @Self
  private SlingHttpServletRequest request;

  /**
   * The image root for this render.
   *
   * @return the asset-library root, or its published copy on a published page.
   */
  @Nonnull
  public String getRoot() {
    return AcplAssetPaths.root(request == null ? null : request.getRequestURI());
  }
}
