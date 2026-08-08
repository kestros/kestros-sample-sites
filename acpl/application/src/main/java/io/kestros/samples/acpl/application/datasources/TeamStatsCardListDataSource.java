package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.TeamService;
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
 * Season-statistics tiles for a team-detail page. Thin adapter over
 * {@link TeamService#getTeamStatTiles}; each tile is a {@link SyntheticStatCard} rendered by the
 * {@code stat-tile} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamStatsCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(TeamStatsCardListDataSource.class);

  @OSGiService
  @Optional
  private TeamService teamService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (teamService == null) {
      return cards;
    }
    int i = 0;
    for (final Map<String, String> tile : teamService.getTeamStatTiles(getTeam())) {
      try {
        cards.add(new SyntheticStatCard(tile.get("value"), tile.get("label"),
            this, "stat", "stat-" + i));
        i++;
      } catch (final Exception e) {
        LOG.error("TeamStatsCardListDataSource: {}", e.getMessage());
        // null-safe
      }
    }
    return cards;
  }
}
