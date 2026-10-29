package io.kestros.samples.acpl.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The matchweek groups (Results, Schedule) and club cards (Clubs) must render on a framework with
 * no ACPL card layout of its own - ACPL Framework 0.0.1 on p3 - where the stock card layout draws
 * only the title and button group it finds as child resources.
 *
 * <p>Every assertion below fails against the code before #1262: both cards returned no elements
 * and no children, so p3 rendered 17 results, 5 fixtures and 12 clubs as empty card bodies.
 */
public class StockCardFallbackTest {

  private static final String BASE = "/content/sites/acpl-p3-datasources";

  @Rule
  public SlingContext context = new SlingContext();

  private BaseSlingModelDataSource dataSource;

  @Before
  public void setUp() {
    final Resource resource = context.create().resource("/content/acpl/jcr:content/cards");
    dataSource = mock(BaseSlingModelDataSource.class);
    when(dataSource.getResourceResolver()).thenReturn(context.resourceResolver());
    when(dataSource.getResource()).thenReturn(resource);
    when(dataSource.getUiFramework()).thenReturn(mock(UiFramework.class));
    when(dataSource.getLayout(anyString())).thenReturn("default");
    when(dataSource.getElementVariations(anyString(), anyString())).thenReturn(new ArrayList<>());
    when(dataSource.getComponentVariationRetrievalService()).thenReturn(
        mock(ComponentVariationRetrievalService.class));
    when(dataSource.getComponentUiFrameworkViewRetrievalService()).thenReturn(
        mock(ComponentUiFrameworkViewRetrievalService.class));
  }

  private static Map<String, String> row(final String home, final String away, final String key,
      final String value, final String id) {
    final Map<String, String> m = new LinkedHashMap<>();
    m.put("homeName", home);
    m.put("awayName", away);
    m.put(key, value);
    m.put("href", BASE + "/matches/" + id + ".html");
    return m;
  }

  @Test
  public void testResultsGroupHasMatchweekTitleAndOneButtonPerMatch() throws Exception {
    final List<Map<String, String>> matches = Arrays.asList(
        row("Harborside FC", "Oakvale Town", "score", "2 – 1", "1337"),
        row("Northgate City", "Albion Rovers", "score", "0 – 0", "1343"));
    final SyntheticMatchweekGroup group = new SyntheticMatchweekGroup("results-group",
        "Matchweek 17", BASE, "", matches, dataSource, "card", "mw-0");

    assertEquals("Matchweek 17", group.getTitleElement().getHeadingText());
    final KestrosButtonGroup buttons = group.getButtonGroupElement();
    assertEquals("one button per match", 2, buttons.getButtonsElements().size());
    assertEquals("Harborside FC 2 – 1 Oakvale Town",
        buttons.getButtonsElements().get(0).getText());
    assertEquals(BASE + "/matches/1337.html", buttons.getButtonsElements().get(0).getHref());

    final List<KestrosBasicComponentElement> children = group.getChildElements();
    assertEquals("title and button group are children", 2, children.size());
  }

  @Test
  public void testFixturesGroupLabelsTheKickoff() throws Exception {
    final SyntheticMatchweekGroup group = new SyntheticMatchweekGroup("fixtures-group",
        "Matchweek 18", BASE, "",
        Arrays.asList(row("Riverside", "Bayview", "time", "15:00", "1380")), dataSource, "card",
        "mw-0");

    assertEquals("Riverside v Bayview · 15:00",
        group.getButtonGroupElement().getButtonsElements().get(0).getText());
  }

  @Test
  public void testGroupWithNoLinkedMatchesHasOnlyItsTitle() throws Exception {
    final Map<String, String> unlinked = row("Riverside", "Bayview", "time", "15:00", "x");
    unlinked.remove("href");
    final SyntheticMatchweekGroup group = new SyntheticMatchweekGroup("fixtures-group",
        "Matchweek 18", BASE, "", Arrays.asList(unlinked), dataSource, "card", "mw-0");

    assertNull("no link, no button group", group.getButtonGroupElement());
    final List<KestrosBasicComponentElement> children = group.getChildElements();
    assertEquals(1, children.size());
  }

  @Test
  public void testClubCardHasNameTitleAndClubPageButton() throws Exception {
    final SyntheticClubCard club = new SyntheticClubCard("harborside", "Harborside FC",
        "Harborside", "Quay Park", "A Manager", "1", "12-3-2", BASE,
        BASE + "/teams/harborside.html", dataSource, "card", "club-0");

    assertEquals("Harborside FC", club.getTitleElement().getHeadingText());
    assertEquals(BASE + "/teams/harborside.html",
        club.getButtonGroupElement().getButtonsElements().get(0).getHref());
    final List<KestrosBasicComponentElement> children = club.getChildElements();
    assertEquals("title and button group are children", 2, children.size());
    assertTrue("button group second", children.get(1) instanceof KestrosButtonGroup);
  }

  @Test
  public void testMatchLabelToleratesMissingNames() {
    assertEquals(" v ", LeagueCardElements.matchLabel(new LinkedHashMap<>()));
  }
}
