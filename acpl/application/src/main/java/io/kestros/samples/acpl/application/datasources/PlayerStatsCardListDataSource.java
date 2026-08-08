package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.PlayerService;
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
 * Current-season statistic tiles for a player-detail page. Thin adapter over
 * {@link PlayerService#getPlayerStatTiles}; reuses {@link SyntheticStatCard} + the {@code stat-tile}
 * card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerStatsCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(PlayerStatsCardListDataSource.class);

  @OSGiService
  @Optional
  private PlayerService playerService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (playerService == null) {
      return cards;
    }
    int i = 0;
    for (final Map<String, String> tile
        : playerService.getPlayerStatTiles(getParam("player", "harborside-mateo-reyes"))) {
      try {
        cards.add(new SyntheticStatCard(tile.get("value"), tile.get("label"),
            this, "stat", "pstat-" + i));
        i++;
      } catch (final Exception e) {
        LOG.error("PlayerStatsCardListDataSource: {}", e.getMessage());
        // null-safe
      }
    }
    return cards;
  }
}
