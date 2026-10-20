package io.kestros.samples.acpl.core.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Card #1623: ACPL images come from the asset library, never from inside a site, and a published
 * page reads them from the published copy.
 *
 * <p>Against the code before #1623 every image was {@code /content/sites/acpl/assets/...}, inside
 * the reference site, whatever the page being rendered.
 */
public class AssetPathsTest {

  private static final String AUTHORED =
      "/content/sites/acpl-authored-p5/jcr:content/main/stories/latest";
  private static final String PUBLISHED =
      "/public/sites/acpl-authored-p5/jcr:content/main/stories/latest";

  private StoryServiceImpl storyService;
  private TeamServiceImpl teamService;

  @Before
  public void setUp() throws Exception {
    final LeagueDataServiceImpl leagueDataService = new LeagueDataServiceImpl();
    leagueDataService.activate();
    storyService = inject(new StoryServiceImpl(), StoryServiceImpl.class, leagueDataService);
    teamService = inject(new TeamServiceImpl(), TeamServiceImpl.class, leagueDataService);
  }

  @Test
  public void storyImagesOnAnAuthoredPageComeFromTheAssetLibrary() {
    final List<Map<String, String>> cards = storyService.getStoryCards(AUTHORED, 3);

    assertFalse(cards.isEmpty());
    for (final Map<String, String> card : cards) {
      assertTrue(card.get("image"), card.get("image").startsWith("/content/assets/acpl/"));
    }
  }

  @Test
  public void storyImagesOnAPublishedPageComeFromThePublishedAssetLibrary() {
    final List<Map<String, String>> cards = storyService.getStoryCards(PUBLISHED, 3);

    assertFalse(cards.isEmpty());
    for (final Map<String, String> card : cards) {
      assertTrue(card.get("image"), card.get("image").startsWith("/public/assets/acpl/"));
    }
  }

  @Test
  public void featuredStoryImageFollowsTheRenderedPath() {
    assertTrue(storyService.getFeaturedStoryCard(AUTHORED).get("image")
        .startsWith("/content/assets/acpl/"));
    assertTrue(storyService.getFeaturedStoryCard(PUBLISHED).get("image")
        .startsWith("/public/assets/acpl/"));
  }

  @Test
  public void squadPortraitsFollowTheRenderedPath() {
    final List<Map<String, String>> authored = teamService.getSquadRows("harborside", AUTHORED);
    final List<Map<String, String>> published = teamService.getSquadRows("harborside", PUBLISHED);

    assertFalse(authored.isEmpty());
    assertTrue(authored.get(0).get("portrait").startsWith("/content/assets/acpl/portraits/"));
    assertTrue(published.get(0).get("portrait").startsWith("/public/assets/acpl/portraits/"));
  }

  @Test
  public void noImageEverPointsInsideASite() {
    for (final Map<String, String> card : storyService.getStoryCards(AUTHORED, 0)) {
      assertFalse(card.get("image"), card.get("image").contains("/sites/"));
    }
  }

  private static <T> T inject(final T service, final Class<T> type,
      final LeagueDataServiceImpl leagueDataService) throws Exception {
    final Field field = type.getDeclaredField("leagueDataService");
    field.setAccessible(true);
    field.set(service, leagueDataService);
    return service;
  }
}
