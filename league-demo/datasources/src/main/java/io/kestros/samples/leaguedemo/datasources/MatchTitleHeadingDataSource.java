package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchTitleHeadingDataSource extends BaseSlingModelDataSource
    implements KestrosHeading {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getHeadingText() {
    if (leagueDataService == null) return "Match";
    String id = (String) getRequest().getAttribute("match-id");
    if (id == null) return "Match";
    Match m = leagueDataService.getMatch(id);
    if (m == null) return id;
    Team home = leagueDataService.getTeam(m.getHomeTeamId());
    Team away = leagueDataService.getTeam(m.getAwayTeamId());
    String homeName = home != null ? home.getName() : m.getHomeTeamId();
    String awayName = away != null ? away.getName() : m.getAwayTeamId();
    if (m.isPlayed()) {
      return homeName + " " + m.getHomeScore() + "-" + m.getAwayScore() + " " + awayName;
    }
    return homeName + " vs " + awayName;
  }

  @Nullable
  @Override
  public String getHeadingType() {
    return getResource().getValueMap().get("level", "h1");
  }
}
