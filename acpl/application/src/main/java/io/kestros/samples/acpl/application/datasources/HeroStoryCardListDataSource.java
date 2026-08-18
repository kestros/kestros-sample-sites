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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The featured story as the home-page hero. Thin adapter over
 * {@link StoryService#getFeaturedStoryCard}; one {@link SyntheticHeroStoryCard} rendered by the
 * {@code story-hero} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class HeroStoryCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(HeroStoryCardListDataSource.class);

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
      cards.add(new SyntheticHeroStoryCard(s.get("headline"), s.get("excerpt"), s.get("image"),
          s.get("href"), s.get("category"), s.get("byline"), this, "hero", "story-hero"));
    } catch (final Exception e) {
      LOG.error("element could not be built: {}", e.getMessage(), e);
      // null-safe
    }
    return cards;
  }
}
