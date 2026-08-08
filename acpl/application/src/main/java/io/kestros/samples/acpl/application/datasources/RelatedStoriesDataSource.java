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
 * Related-stories card list for a story page (stories sharing a club, backfilled with other recent
 * stories). Thin adapter over {@link StoryService#getRelatedStoryCards}; the current story comes from
 * the node's {@code story} property, count from {@code limit} (default 3).
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class RelatedStoriesDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(RelatedStoriesDataSource.class);

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
    final String story = getResource().getValueMap().get("story", "");
    final int limit = getResource().getValueMap().get("limit", 3);
    int i = 0;
    for (final Map<String, String> s
        : storyService.getRelatedStoryCards(story, contextPath(), limit)) {
      try {
        cards.add(new SyntheticStoryCard(s.get("headline"), s.get("excerpt"), s.get("image"),
            s.get("href"), s.get("category"), s.get("byline"), this, "card", "rel-" + i));
        i++;
      } catch (final Exception e) {
        LOG.error("row skipped: {}", e.getMessage(), e);
        // null-safe
      }
    }
    return cards;
  }
}
