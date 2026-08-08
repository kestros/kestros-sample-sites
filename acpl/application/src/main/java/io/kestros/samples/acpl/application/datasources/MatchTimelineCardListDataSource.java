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
 * Goals as timeline dots on a minutes axis. Thin adapter over
 * {@link MatchService#getGoalRows} (which supplies leftPct/side/color); rendered by the
 * {@code timeline} list layout + {@code goal-dot} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchTimelineCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(MatchTimelineCardListDataSource.class);

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
            g.get("teamSlug"), g.get("assist"), g.get("base"), g.get("side"), this, "tl",
            "tl-" + i).asTimelineDot(g.get("leftPct"), g.get("color")));
        i++;
      } catch (final Exception e) {
        LOG.error("row skipped: {}", e.getMessage(), e);
        // null-safe
      }
    }
    return cards;
  }
}
