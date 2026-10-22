package io.kestros.samples.acpl.api.sites;

/**
 * Where an ACPL site lives. A site is authored under {@code /content/sites/<site>} and published
 * to {@code /public/sites/<site>}; every site-root match in ACPL code accepts both.
 */
public final class AcplSitePaths {

  /**
   * Regex for the folder that holds sites, authored or published. Compile-time constant so the
   * dynamic-page filters can build their Sling filter patterns from it.
   */
  public static final String SITES = "/(?:content|public)/sites";

  private AcplSitePaths() {
  }
}
