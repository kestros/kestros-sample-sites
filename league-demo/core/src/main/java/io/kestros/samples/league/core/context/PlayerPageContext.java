package io.kestros.samples.league.core.context;

import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = SlingHttpServletRequest.class)
public class PlayerPageContext {

  private final SlingHttpServletRequest request;

  @OSGiService
  private LeagueDataService leagueDataService;

  private Player player;
  private Team team;

  public PlayerPageContext(SlingHttpServletRequest request) {
    this.request = request;
  }

  @PostConstruct
  protected void init() {
    String slug = (String) request.getAttribute("player-slug");
    if (slug != null && leagueDataService != null) {
      player = leagueDataService.getPlayer(slug);
      if (player != null) {
        team = leagueDataService.getTeam(player.getTeamId());
      }
    }
  }

  @Nullable
  public Player getPlayer() {
    return player;
  }

  @Nullable
  public Team getTeam() {
    return team;
  }
}
