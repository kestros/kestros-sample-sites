package io.kestros.samples.league.core.context;

import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = SlingHttpServletRequest.class)
public class MatchPageContext {

  private final SlingHttpServletRequest request;

  @OSGiService
  private LeagueDataService leagueDataService;

  private Match match;
  private Team homeTeam;
  private Team awayTeam;

  public MatchPageContext(SlingHttpServletRequest request) {
    this.request = request;
  }

  @PostConstruct
  protected void init() {
    String id = (String) request.getAttribute("match-id");
    if (id != null && leagueDataService != null) {
      match = leagueDataService.getMatch(id);
      if (match != null) {
        homeTeam = leagueDataService.getTeam(match.getHomeTeamId());
        awayTeam = leagueDataService.getTeam(match.getAwayTeamId());
      }
    }
  }

  @Nullable
  public Match getMatch() {
    return match;
  }

  @Nullable
  public Team getHomeTeam() {
    return homeTeam;
  }

  @Nullable
  public Team getAwayTeam() {
    return awayTeam;
  }
}
