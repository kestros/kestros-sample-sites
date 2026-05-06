package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Players page intro paragraph: dynamically composed from the in-progress season name,
 * registered player and club counts, and the current Golden Boot leader. Demonstrates
 * how a single text component can present a rich, always-current narrative without any
 * authoring updates as the season progresses.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayersIntroTextDataSource extends BaseSlingModelDataSource
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

    String seasonName = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .map(Season::getName)
        .orElse("");

    int playerCount = leagueDataService.getPlayers().size();
    int teamCount = leagueDataService.getTeams().size();

    Optional<Player> leader = leagueDataService.getPlayers().stream()
        .filter(p -> p.getGoals() > 0)
        .max(Comparator.comparingInt(Player::getGoals));

    StringBuilder sb = new StringBuilder();
    sb.append("The ");
    if (!seasonName.isEmpty()) {
      sb.append(seasonName).append(" ");
    }
    sb.append("Meridian Premier League features ")
      .append(playerCount)
      .append(" registered players across ")
      .append(teamCount)
      .append(" clubs -- from veteran goalscorers chasing the Golden Boot to academy "
          + "graduates making their professional debuts.");

    leader.ifPresent(p -> sb.append(" Leading the scoring charts: ")
        .append(p.getFirstName())
        .append(' ')
        .append(p.getLastName())
        .append(" with ")
        .append(p.getGoals())
        .append(" goals."));

    return sb.toString();
  }
}
