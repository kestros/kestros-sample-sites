package io.kestros.samples.acpl.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Covers the club cell display modes on card #960.
 *
 * <p>The ACPL p3 home page runs on ACPL Framework 0.0.1 — bare Bootstrap — and that version carries
 * no {@code club} table-cell layout. A {@code rich-*} display mode builds a cell whose own text is
 * empty and puts the club name in {@code properties.label}, which only the {@code club} layout
 * renders, so on 0.0.1 the Club column comes out blank. {@code name} is the mode that gives the full
 * club name as ordinary cell text, which is what the page needs.
 *
 * <p>These run against the real bundled {@code /data} resources, on the test classpath from
 * {@code src/main/resources}.
 */
public class StandingsServiceImplTest {

  private static final String SLUG = "harborside";
  private static final String CONTEXT_PATH =
      "/content/sites/acpl-p3-datasources/jcr:content/main/dashboard/grid/column-2/table";

  private StandingsServiceImpl service;
  private LeagueDataServiceImpl leagueDataService;

  @Before
  public void setUp() throws Exception {
    leagueDataService = new LeagueDataServiceImpl();
    leagueDataService.activate();

    service = new StandingsServiceImpl();
    final Field field = StandingsServiceImpl.class.getDeclaredField("leagueDataService");
    field.setAccessible(true);
    field.set(service, leagueDataService);
  }

  /**
   * The behaviour this card adds. Against unmodified code {@code name} fell through to the
   * abbreviation branch, so this asserted "Harborside United" and got "HAR".
   */
  @Test
  public void nameGivesTheFullClubNameSoItRendersAsPlainCellText() {
    final Map<String, String> cell = service.getClubCell(SLUG, "name", CONTEXT_PATH);

    assertEquals(leagueDataService.getClubName(SLUG), cell.get("label"));
    assertNotEquals(leagueDataService.getClubShort(SLUG), cell.get("label"));
  }

  /** {@code rich-name} is unchanged — p4 and p5 still use it against the {@code club} layout. */
  @Test
  public void richNameStillGivesTheFullNameAndTheRichLinkClass() {
    final Map<String, String> cell = service.getClubCell(SLUG, "rich-name", CONTEXT_PATH);

    assertEquals(leagueDataService.getClubName(SLUG), cell.get("label"));
    assertEquals("fw-semibold club-link", cell.get("linkClass"));
  }

  /** No display mode is still the abbreviation, as the standings sub-page relies on. */
  @Test
  public void noDisplayModeIsStillTheAbbreviation() {
    final Map<String, String> cell = service.getClubCell(SLUG, "", CONTEXT_PATH);

    assertEquals(leagueDataService.getClubShort(SLUG), cell.get("label"));
    assertEquals("club-link", cell.get("linkClass"));
  }

  /** A null display mode must not throw — the datasource defaults it, but the service is public. */
  @Test
  public void nullDisplayModeIsTreatedAsNoDisplayMode() {
    final Map<String, String> cell = service.getClubCell(SLUG, null, CONTEXT_PATH);

    assertEquals(leagueDataService.getClubShort(SLUG), cell.get("label"));
  }
}
