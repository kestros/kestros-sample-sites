package io.kestros.samples.acpl.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import java.util.ArrayList;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The stock card layout renders the title, image and button-group elements rather than
 * {@code properties.*}, so a card that has the strings but no elements renders as an empty box.
 *
 * <p>Every assertion below that expects a non-null element fails against the code before
 * {@code #960}: the three getters returned null unconditionally.
 */
public class LeagueCardElementsTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseSlingModelDataSource dataSource;

  @Before
  public void setUp() {
    final Resource resource = context.create().resource("/content/acpl/jcr:content/cards");
    dataSource = mock(BaseSlingModelDataSource.class);
    // BaseSyntheticResource throws ComponentConfigurationException if any of these is null, and
    // every factory below turns that exception into a null element - so an under-stubbed mock
    // would make the whole test pass for the wrong reason.
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

  @Test
  public void testHeadingCarriesTheCardTitle() {
    final KestrosHeading heading = LeagueCardElements.heading("Ashford Town", dataSource);

    assertNotNull("a card with a title gets a heading element", heading);
    assertEquals("Ashford Town", heading.getHeadingText());
    assertEquals("h3", heading.getHeadingType());
  }

  @Test
  public void testHeadingIsNullWhenThereIsNoTitle() {
    assertNull(LeagueCardElements.heading(null, dataSource));
    assertNull(LeagueCardElements.heading("", dataSource));
    assertNull(LeagueCardElements.heading("   ", dataSource));
  }

  @Test
  public void testImageCarriesThePathAndAltText() {
    final KestrosImage image = LeagueCardElements.image("/content/acpl/img/ashford.png",
        "Ashford Town", "/content/sites/acpl-p3-datasources/clubs/ashford.html", dataSource);

    assertNotNull("a card with an image path gets an image element", image);
  }

  @Test
  public void testImageIsNullWhenThereIsNoPath() {
    assertNull(LeagueCardElements.image(null, "Ashford Town", "/somewhere.html", dataSource));
    assertNull(LeagueCardElements.image("", "Ashford Town", "/somewhere.html", dataSource));
  }

  @Test
  public void testButtonGroupHoldsOneButtonPointingAtTheCard() {
    final KestrosButtonGroup buttonGroup = LeagueCardElements.buttonGroup("Match centre",
        "/content/sites/acpl-p3-datasources/matches/m1.html", dataSource);

    assertNotNull("a card with an href gets a button group", buttonGroup);
    assertEquals("one card, one button", 1, buttonGroup.getButtonsElements().size());
    assertEquals("Match centre", buttonGroup.getButtonsElements().get(0).getText());
    assertEquals("/content/sites/acpl-p3-datasources/matches/m1.html",
        buttonGroup.getButtonsElements().get(0).getHref());
  }

  @Test
  public void testButtonGroupIsNullWhenTheCardLinksNowhere() {
    assertNull(LeagueCardElements.buttonGroup("Read more", null, dataSource));
    assertNull(LeagueCardElements.buttonGroup("Read more", "", dataSource));
  }

  @Test
  public void testStoryCardFillsInAllThreeElements() throws Exception {
    final SyntheticStoryCard card = new SyntheticStoryCard("Ashford hold on at the top",
        "Two late saves keep the lead intact.", "/content/acpl/img/story-1.png",
        "/content/sites/acpl-p3-datasources/stories/story-1.html", "Match report", "A Reporter",
        dataSource, "card", null);

    assertNotNull("story card heading", card.getTitleElement());
    assertEquals("Ashford hold on at the top", card.getTitleElement().getHeadingText());
    assertNotNull("story card image", card.getImageElement());
    assertNotNull("story card button", card.getButtonGroupElement());
  }

  @Test
  public void testMatchCardHeadingCarriesTheScoreWhenThePlayedMatchHasOne() throws Exception {
    final SyntheticMatchCard played = matchCard("2", "1");

    assertNotNull("match card heading", played.getTitleElement());
    assertEquals("Ashford Town 2 - 1 Bexley United", played.getTitleElement().getHeadingText());
    assertNotNull("match card button", played.getButtonGroupElement());
  }

  @Test
  public void testMatchCardHeadingIsTheFixtureWhenThereIsNoScore() throws Exception {
    final SyntheticMatchCard upcoming = matchCard("", "");

    assertNotNull("match card heading", upcoming.getTitleElement());
    assertEquals("Ashford Town v Bexley United", upcoming.getTitleElement().getHeadingText());
  }

  private SyntheticMatchCard matchCard(final String homeGoals, final String awayGoals)
      throws Exception {
    return new SyntheticMatchCard("match-card", "/content/sites/acpl-p3-datasources",
        "/content/sites/acpl-p3-datasources/matches/m1.html", "ashford", "ASH", "Ashford Town",
        "bexley", "BEX", "Bexley United", "m1", dataSource, "card", null).withGoals(homeGoals,
        awayGoals);
  }
}
