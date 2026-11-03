package io.kestros.samples.acpl.core.services;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Covers the site root every ACPL link is built from.
 *
 * <p>A published page renders from {@code /public/sites/<site>}, not {@code /content/sites/<site>}.
 * The site root used to be matched against {@code /content/sites/} only, so on a published page it
 * fell back to the whole component path, and every team, match and story link came out as
 * {@code /public/sites/<site>/jcr:content/.../teams/<club>.html}, which 404s.
 */
public class AbstractDisplayServiceSiteRootTest {

  private static final String COMPONENT =
      "/jcr:content/main/dashboard/grid/column-2/table";

  private final AbstractDisplayService display = new AbstractDisplayService() { };

  private StandingsServiceImpl standings;

  @Before
  public void setUp() throws Exception {
    final LeagueDataServiceImpl leagueDataService = new LeagueDataServiceImpl();
    leagueDataService.activate();
    standings = new StandingsServiceImpl();
    final Field field = StandingsServiceImpl.class.getDeclaredField("leagueDataService");
    field.setAccessible(true);
    field.set(standings, leagueDataService);
  }

  @Test
  public void authorPathGivesTheContentSiteRoot() {
    assertEquals("/content/sites/acpl-p5-bulma-versions",
        display.siteRoot("/content/sites/acpl-p5-bulma-versions" + COMPONENT));
  }

  /** Against unmodified code this returned the whole component path. */
  @Test
  public void publishedPathGivesThePublicSiteRoot() {
    assertEquals("/public/sites/acpl-p5-bulma-versions",
        display.siteRoot("/public/sites/acpl-p5-bulma-versions" + COMPONENT));
  }

  @Test
  public void siteRootPathItselfIsItsOwnRoot() {
    assertEquals("/public/sites/acpl", display.siteRoot("/public/sites/acpl"));
  }

  /** The base a published club link is built from, as the standings datasource hands it out. */
  @Test
  public void publishedClubCellLinksFromThePublicSiteRoot() {
    final Map<String, String> cell = standings.getClubCell("harborside", "name",
        "/public/sites/acpl-p5-bulma-versions" + COMPONENT);

    assertEquals("/public/sites/acpl-p5-bulma-versions", cell.get("base"));
  }

  @Test
  public void nullPathGivesAnEmptyRoot() {
    assertEquals("", display.siteRoot(null));
  }
}
