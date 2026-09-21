package io.kestros.samples.acpl.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import java.util.List;
import org.junit.Test;

/**
 * A league card has to hand its title, image and button group out as child elements.
 *
 * <p>They are only reachable to the renderer that way: {@code toSyntheticResource} turns each child
 * element into a child resource, and the stock card layout reads them back through
 * {@code CardStaticDataSource}, which looks for children named {@code titleElement},
 * {@code imageElement} and {@code buttonGroup}. While {@code getChildElements()} returned an empty
 * list the elements existed on the object and never reached the resource, so a card on a UI
 * framework with no card layout of its own rendered an empty {@code card-body} — which is what the
 * ACPL p3 fixtures and results lists were doing.
 *
 * <p>These mock the card rather than building one: a real {@link AbstractLeagueCard} needs an
 * ancestor page that sling-mock does not provide, as
 * {@code ClubDirectoryCardListDataSourceTest} records.
 */
public class LeagueCardChildElementsTest {

  private final KestrosHeading heading = mock(KestrosHeading.class);

  private final KestrosImage image = mock(KestrosImage.class);

  private final KestrosButtonGroup buttonGroup = mock(KestrosButtonGroup.class);

  @Test
  public void testLeagueCardHandsOutTitleImageAndButtonGroup() {
    final AbstractLeagueCard card = mock(AbstractLeagueCard.class, CALLS_REAL_METHODS);
    doReturn(heading).when(card).getTitleElement();
    doReturn(image).when(card).getImageElement();
    doReturn(buttonGroup).when(card).getButtonGroupElement();

    final List<KestrosBasicComponentElement> children = card.getChildElements();

    assertEquals("title, image and button group are all children", 3, children.size());
    assertSame(heading, children.get(0));
    assertSame(image, children.get(1));
    assertSame(buttonGroup, children.get(2));
  }

  @Test
  public void testLeagueCardOmitsTheElementsItDoesNotHave() {
    // A null element must be left out rather than added as a null child: toSyntheticResource calls
    // toSyntheticResource on every child it is given.
    final AbstractLeagueCard card = mock(AbstractLeagueCard.class, CALLS_REAL_METHODS);
    doReturn(null).when(card).getTitleElement();
    doReturn(null).when(card).getImageElement();
    doReturn(buttonGroup).when(card).getButtonGroupElement();

    final List<KestrosBasicComponentElement> children = card.getChildElements();

    assertEquals("only the button group survives", 1, children.size());
    assertSame(buttonGroup, children.get(0));
  }

  @Test
  public void testLeagueCardWithNoElementsHasNoChildren() {
    final AbstractLeagueCard card = mock(AbstractLeagueCard.class, CALLS_REAL_METHODS);
    doReturn(null).when(card).getTitleElement();
    doReturn(null).when(card).getImageElement();
    doReturn(null).when(card).getButtonGroupElement();

    assertTrue("nothing to render, nothing to hand out", card.getChildElements().isEmpty());
  }

  @Test
  public void testMatchCardHandsOutTitleAndButtonGroup() {
    // SyntheticMatchCard does not extend AbstractLeagueCard and carried its own empty override, so
    // it needs its own assertion. It never has an image.
    final SyntheticMatchCard card = mock(SyntheticMatchCard.class, CALLS_REAL_METHODS);
    doReturn(heading).when(card).getTitleElement();
    doReturn(buttonGroup).when(card).getButtonGroupElement();

    final List<KestrosBasicComponentElement> children = card.getChildElements();

    assertEquals("title and button group are children", 2, children.size());
    assertSame(heading, children.get(0));
    assertSame(buttonGroup, children.get(1));
  }

  @Test
  public void testMatchCardOmitsAMissingTitle() {
    final SyntheticMatchCard card = mock(SyntheticMatchCard.class, CALLS_REAL_METHODS);
    doReturn(null).when(card).getTitleElement();
    doReturn(buttonGroup).when(card).getButtonGroupElement();

    final List<KestrosBasicComponentElement> children = card.getChildElements();

    assertEquals("only the button group survives", 1, children.size());
    assertSame(buttonGroup, children.get(0));
  }
}
