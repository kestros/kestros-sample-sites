package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Match report body. For played matches, returns the stored writeup. For upcoming
 * fixtures, returns a brief auto-generated preview (date + venue + opponents) so the
 * Match Report section never renders empty on a fixture detail page.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchWriteupTextDataSource extends BaseSlingModelDataSource implements KestrosText {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    if (leagueDataService == null) return "";
    String id = (String) getRequest().getAttribute("match-id");
    if (id == null) return "";
    Match match = leagueDataService.getMatch(id);
    if (match == null) return "";

    if (match.isPlayed()) {
      String writeup = match.getWriteup();
      return writeup != null ? writeup : "";
    }

    Team home = leagueDataService.getTeam(match.getHomeTeamId());
    Team away = leagueDataService.getTeam(match.getAwayTeamId());
    String homeName = home != null ? home.getName() : match.getHomeTeamId();
    String awayName = away != null ? away.getName() : match.getAwayTeamId();

    StringBuilder sb = new StringBuilder("Matchweek ").append(match.getMatchday())
        .append(" preview: ").append(homeName).append(" host ").append(awayName);
    if (StringUtils.isNotBlank(match.getDate())) {
      sb.append(" on ").append(Dates.medium(match.getDate()));
    }
    if (StringUtils.isNotBlank(match.getVenue())) {
      sb.append(" at ").append(match.getVenue());
    }
    sb.append(". Full match report will be published after the final whistle.");
    return sb.toString();
  }
}
