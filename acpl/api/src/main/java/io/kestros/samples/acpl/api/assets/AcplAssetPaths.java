package io.kestros.samples.acpl.api.assets;

/**
 * Where the ACPL image set lives: one shared set in the asset library (#251, #1623), never inside
 * a site, so a site carries no images of its own and needs no second site to show them.
 *
 * <p>A published page renders under {@code /public}, and its images have to come from the
 * published copy of the asset library, so the root follows the path being rendered.
 */
public final class AcplAssetPaths {

  /** The ACPL image set in the asset library. */
  public static final String CONTENT_ROOT = "/content/assets/acpl";

  /** The published copy of {@link #CONTENT_ROOT}. */
  public static final String PUBLIC_ROOT = "/public/assets/acpl";

  private AcplAssetPaths() {
  }

  /**
   * The image root for a render of {@code path}.
   *
   * @param path the page, component or request path being rendered; may be null.
   * @return {@link #PUBLIC_ROOT} when {@code path} is under {@code /public/}, otherwise
   *     {@link #CONTENT_ROOT}.
   */
  public static String root(final String path) {
    return path != null && path.startsWith("/public/") ? PUBLIC_ROOT : CONTENT_ROOT;
  }
}
