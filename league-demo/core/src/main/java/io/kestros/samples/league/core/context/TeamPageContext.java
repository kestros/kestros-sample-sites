package io.kestros.samples.league.core.context;

import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = SlingHttpServletRequest.class)
public class TeamPageContext {

  private final SlingHttpServletRequest request;

  @OSGiService
  private LeagueDataService leagueDataService;

  private Team team;
  private List<Player> players;

  public TeamPageContext(SlingHttpServletRequest request) {
    this.request = request;
  }

  @PostConstruct
  protected void init() {
    String slug = (String) request.getAttribute("team-slug");
    if (slug != null && leagueDataService != null) {
      team = leagueDataService.getTeam(slug);
      players = leagueDataService.getPlayersByTeam(slug);
    }
  }

  @Nullable
  public Team getTeam() {
    return team;
  }

  public List<Player> getPlayers() {
    return players != null ? players : Collections.emptyList();
  }

  public List<Match> getMatches() {
    if (leagueDataService == null) {
      return Collections.emptyList();
    }
    return leagueDataService.getMatches();
  }
}
