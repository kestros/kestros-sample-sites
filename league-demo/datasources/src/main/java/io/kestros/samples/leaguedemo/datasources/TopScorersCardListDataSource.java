package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TopScorersCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  private static final int MAX_PLAYERS = 5;

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    List<Player> topScorers = leagueDataService.getPlayers().stream()
        .filter(p -> p.getGoals() > 0)
        .sorted(Comparator.comparingInt(Player::getGoals).reversed())
        .limit(MAX_PLAYERS)
        .collect(Collectors.toList());

    for (int i = 0; i < topScorers.size(); i++) {
      Player player = topScorers.get(i);
      Team team = leagueDataService.getTeam(player.getTeamId());
      String teamName = team != null ? team.getName() : player.getTeamId();

      try {
        String name = player.getFirstName() + " " + player.getLastName();
        // Card titleElement doesn't render through synthetic resource adaptation,
        // so include name in description text where it actually shows.
        String desc = (i + 1) + ". " + name + " (" + teamName + ") -- "
            + player.getGoals() + " goals, " + player.getAssists() + " assists in "
            + player.getAppearances() + " apps";

        cards.add(new KestrosCardImpl(
            desc,
            new KestrosHeadingImpl(name,
                "h3", this, "title", "scorer-title-" + i),
            null, null, this, "card", player.getId()));
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
