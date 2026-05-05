package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.cms.components.basic.core.content.image.KestrosImageImpl;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TopScorersCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  private static final int DEFAULT_MAX_PLAYERS = 5;

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  int getMaxPlayers() {
    return getResource().getValueMap().get("maxRows", DEFAULT_MAX_PLAYERS);
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    List<Player> topScorers = leagueDataService.getPlayers().stream()
        .filter(p -> p.getGoals() > 0)
        .sorted(Comparator.comparingInt(Player::getGoals).reversed())
        .limit(getMaxPlayers())
        .collect(Collectors.toList());

    for (int i = 0; i < topScorers.size(); i++) {
      Player player = topScorers.get(i);
      Team team = leagueDataService.getTeam(player.getTeamId());
      String teamName = team != null ? team.getName() : player.getTeamId();

      try {
        String name = player.getFirstName() + " " + player.getLastName();
        String goalsStr = player.getGoals() == 1 ? "1 goal" : player.getGoals() + " goals";
        String assistsStr = player.getAssists() == 1 ? "1 assist" : player.getAssists() + " assists";
        String appsStr = player.getAppearances() == 1 ? "1 app" : player.getAppearances() + " apps";
        String desc = teamName + " | " + goalsStr + ", " + assistsStr + " in " + appsStr;

        KestrosImage image = null;
        if (StringUtils.isNotBlank(player.getImageUrl())) {
          try {
            image = new KestrosImageImpl(
                player.getImageUrl(), name, null, null,
                null, null, null, AnchorTarget.SAME_WINDOW,
                this, "image", "imageElement", null);
          } catch (Exception ignored) {}
        }

        cards.add(new KestrosCardImpl(
            desc,
            new KestrosHeadingImpl((i + 1) + ". " + name,
                "h3", this, "title", "titleElement"),
            image, null, this, "card", player.getId()));
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
