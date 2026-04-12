package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerStatsCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private Player getPlayer() {
    if (leagueDataService == null) return null;
    String slug = (String) getRequest().getAttribute("player-slug");
    return slug != null ? leagueDataService.getPlayer(slug) : null;
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    Player player = getPlayer();
    if (player == null) return cards;

    // For now stats are placeholder - will be enriched with actual per-player stats later
    String[][] stats = {
        {"Apps", "12"},
        {"Goals", "8"},
        {"Assists", "3"},
        {"Mins/Goal", "135"},
    };

    int i = 0;
    for (String[] stat : stats) {
      try {
        cards.add(new KestrosCardImpl(
            stat[1],
            new KestrosHeadingImpl(stat[0], "h4", this, "title", "stat-title-" + i),
            null, null,
            this, "card", "stat-" + i));
        i++;
      } catch (Exception e) {
        // skip on error
      }
    }
    return cards;
  }
}
