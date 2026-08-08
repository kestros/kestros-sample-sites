package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.ScheduleService;
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
 * Compact match list for the home widgets ({@code mode="results"} or {@code "fixtures"}, optional
 * {@code limit}). Thin adapter over {@link ScheduleService}; rows are {@link SyntheticMatchCard}s
 * rendered by the {@code result-row} / {@code fixture-row} card layouts.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(MatchListDataSource.class);

  @OSGiService
  @Optional
  private ScheduleService scheduleService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (scheduleService == null) {
      return cards;
    }
    final boolean fixtures = "fixtures".equals(getResource().getValueMap().get("mode", "results"));
    final int limit = getResource().getValueMap().get("limit", 0);
    final boolean chip = "true".equals(getResource().getValueMap().get("chip", ""));
    final String layout = chip ? "score-chip" : (fixtures ? "fixture-row" : "result-row");
    final List<Map<String, String>> rows = fixtures
        ? scheduleService.getUpcomingFixtureRows(contextPath(), limit)
        : scheduleService.getRecentResultRows(contextPath(), limit);
    int i = 0;
    for (final Map<String, String> m : rows) {
      try {
        cards.add(new SyntheticMatchCard(layout, m.get("base"), m.get("href"),
            m.get("homeSlug"), m.get("homeShort"), m.get("homeName"),
            m.get("awaySlug"), m.get("awayShort"), m.get("awayName"),
            m.get("mid"), this, "card", "match-" + i).withGoals(m.get("hg"), m.get("ag")));
        i++;
      } catch (final Exception e) {
        LOG.error("MatchListDataSource: {}", e.getMessage());
        // null-safe: skip a match that fails to build
      }
    }
    return cards;
  }
}
