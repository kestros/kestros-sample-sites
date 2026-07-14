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
 * The featured (hero) story for the stories landing page. Thin adapter over
 * {@link StoryService#getFeaturedStoryCard}; one {@link SyntheticFeaturedStoryCard} rendered by the
 * {@code featured-story} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class FeaturedStoryCardListDataSource extends AbstractLeagueCardListDataSource {

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
    final Map<String, String> s = storyService.getFeaturedStoryCard(contextPath());
    if (s.isEmpty()) {
      return cards;
    }
    try {
      cards.add(new SyntheticFeaturedStoryCard(s.get("headline"), s.get("excerpt"), s.get("image"),
          s.get("href"), s.get("category"), s.get("byline"), this, "featured", "featured-story"));
    } catch (final Exception e) {
      // null-safe
    }
    return cards;
  }
}
