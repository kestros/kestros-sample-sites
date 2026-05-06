package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerStatsSummaryTextDataSource extends BaseSlingModelDataSource
    implements KestrosText {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    if (leagueDataService == null) return "";
    String slug = (String) getRequest().getAttribute("player-slug");
    if (slug == null) return "";
    Player p = leagueDataService.getPlayer(slug);
    if (p == null) return "";
    StringBuilder s = new StringBuilder();
    if (p.getPosition() != null) {
      s.append(p.getPosition()).append(" · #").append(p.getNumber());
    }
    Team team = p.getTeamId() != null ? leagueDataService.getTeam(p.getTeamId()) : null;
    if (team != null) {
      if (s.length() > 0) s.append(" · ");
      s.append(team.getName());
    }
    if (s.length() > 0) s.append(" — ");
    int apps = PlayerAppearances.displayedFor(p, leagueDataService);
    s.append(p.getGoals()).append(p.getGoals() == 1 ? " goal, " : " goals, ");
    s.append(p.getAssists()).append(p.getAssists() == 1 ? " assist in " : " assists in ");
    s.append(apps).append(apps == 1 ? " app" : " apps");
    return s.toString();
  }
}
