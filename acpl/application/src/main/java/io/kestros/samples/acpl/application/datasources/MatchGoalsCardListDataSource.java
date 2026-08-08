package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.MatchService;
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
 * Match goals timeline, keyed on the {@code match} route param. Thin adapter over
 * {@link MatchService#getGoalRows}; each goal is a {@link SyntheticGoalCard} rendered by the
 * {@code goal-row} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchGoalsCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(MatchGoalsCardListDataSource.class);

  @OSGiService
  @Optional
  private MatchService matchService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (matchService == null) {
      return cards;
    }
    int i = 0;
    for (final Map<String, String> g
        : matchService.getGoalRows(getParam("match", ""), contextPath())) {
      try {
        cards.add(new SyntheticGoalCard(g.get("minute"), g.get("scorer"), g.get("href"),
            g.get("teamSlug"), g.get("assist"), g.get("base"), g.get("side"), this, "goal",
            "goal-" + i).withPortrait(g.get("portrait")));
        i++;
      } catch (final Exception e) {
        LOG.error("MatchGoalsCardListDataSource: {}", e.getMessage());
        // null-safe
      }
    }
    return cards;
  }
}
