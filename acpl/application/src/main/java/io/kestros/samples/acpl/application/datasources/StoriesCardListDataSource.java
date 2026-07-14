package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.StoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Latest-news card list. Thin adapter over {@link StoryService#getStoryCards}; a {@code limit}
 * property trims the list (e.g. 3 on the home widget). Cards render via the {@code story-card} layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class StoriesCardListDataSource extends AbstractLeagueCardListDataSource {

  @OSGiService
  @Optional
  private StoryService storyService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (storyService == null) {
      return cards;
    }
    final int limit = getResource().getValueMap().get("limit", 0);
    final boolean excludeFeatured =
        getResource().getValueMap().get("excludeFeatured", Boolean.FALSE);
    final List<Map<String, String>> stories = excludeFeatured
        ? storyService.getStoryCardsExcludingFeatured(contextPath(), limit)
        : storyService.getStoryCards(contextPath(), limit);
    int i = 0;
    for (final Map<String, String> s : stories) {
      try {
        cards.add(new SyntheticStoryCard(s.get("headline"), s.get("excerpt"), s.get("image"),
            s.get("href"), s.get("category"), s.get("byline"), this, "card", "story-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe: skip a story that fails to build
      }
    }
    return cards;
  }
}
