package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Players page hero subtitle: "{playerCount} players across {teamCount} clubs. Sorted by
 * goals scored." Both counts come from the LeagueDataService so the subtitle stays accurate
 * when the squad changes.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayersHeroSubtitleTextDataSource extends BaseSlingModelDataSource
    implements KestrosText {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    if (leagueDataService == null) {
      return "";
    }
    int playerCount = leagueDataService.getPlayers().size();
    int teamCount = leagueDataService.getTeams().size();
    return playerCount + " players across " + teamCount
        + " clubs. Sorted by goals scored.";
  }
}
