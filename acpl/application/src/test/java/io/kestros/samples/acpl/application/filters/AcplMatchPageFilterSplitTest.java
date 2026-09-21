package io.kestros.samples.acpl.application.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.sitebuilding.api.models.BaseSite;
import io.kestros.samples.acpl.api.services.LeagueDataService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The match report page and the match preview page are two filter types, because a page names its
 * filter and one filter type is one page. Each filter must decline the fixtures that belong to the
 * other, or both would try to serve the same URL.
 */
public class AcplMatchPageFilterSplitTest {

  private static final String SITE_PATH = "/content/sites/acpl";

  private static final String REQUEST_PATH = SITE_PATH + "/matches/rovers-vs-city";

  @Rule
  public SlingContext context = new SlingContext();

  private LeagueDataService leagueDataService;

  private BaseSite site;

  private AcplMatchPageFilter matchFilter;

  private AcplMatchPreviewPageFilter previewFilter;

  @Before
  public void setUp() {
    leagueDataService = mock(LeagueDataService.class);
    when(leagueDataService.getClubName("rovers")).thenReturn("Riverside Rovers");
    when(leagueDataService.getClubName("city")).thenReturn("Central City");
    context.registerService(LeagueDataService.class, leagueDataService);

    final Resource siteResource = context.create().resource(SITE_PATH);
    site = mock(BaseSite.class);
    when(site.getResource()).thenReturn(siteResource);

    matchFilter = context.registerInjectActivateService(new AcplMatchPageFilter());
    previewFilter = context.registerInjectActivateService(new AcplMatchPreviewPageFilter());
  }

  private void givenMatch(final boolean played) {
    final Map<String, Object> match = new HashMap<>();
    match.put("played", played);
    match.put("home", "rovers");
    match.put("away", "city");
    match.put("hg", 2);
    match.put("ag", 1);
    match.put("venue", "Riverside Park");
    when(leagueDataService.getMatch("rovers-vs-city")).thenReturn(match);
  }

  @Test
  public void testMatchFilterServesAPlayedFixture() {
    givenMatch(true);

    final Map<String, String> parameters = matchFilter.extractParameters(REQUEST_PATH, site,
        context.request());

    assertNotNull(parameters);
    assertEquals("rovers-vs-city", parameters.get("match"));
    assertEquals("Riverside Rovers 2–1 Central City", parameters.get("dynamicPageTitle"));
  }

  @Test
  public void testMatchFilterDeclinesAnUnplayedFixture() {
    givenMatch(false);

    assertNull(matchFilter.extractParameters(REQUEST_PATH, site, context.request()));
  }

  @Test
  public void testPreviewFilterServesAnUnplayedFixture() {
    givenMatch(false);

    final Map<String, String> parameters = previewFilter.extractParameters(REQUEST_PATH, site,
        context.request());

    assertNotNull(parameters);
    assertEquals("rovers-vs-city", parameters.get("match"));
    assertEquals("Riverside Rovers vs Central City", parameters.get("dynamicPageTitle"));
  }

  @Test
  public void testPreviewFilterDeclinesAPlayedFixture() {
    givenMatch(true);

    assertNull(previewFilter.extractParameters(REQUEST_PATH, site, context.request()));
  }

  @Test
  public void testTheTwoFiltersClaimDifferentPages() {
    assertEquals("acpl-match", matchFilter.getFilterType());
    assertEquals("acpl-match-preview", previewFilter.getFilterType());
  }

  @Test
  public void testAPathOutsideMatchesIsDeclined() {
    givenMatch(true);

    assertNull(matchFilter.extractParameters(SITE_PATH + "/players/someone", site,
        context.request()));
    assertNull(previewFilter.extractParameters(SITE_PATH + "/players/someone", site,
        context.request()));
  }
}
