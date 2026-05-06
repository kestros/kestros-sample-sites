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
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class ChampionsCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    int i = 0;
    for (Season season : leagueDataService.getSeasons()) {
      String champId = season.getChampionId();
      if (champId == null || champId.isEmpty()) continue;

      Team champion = leagueDataService.getTeam(champId);
      if (champion == null) continue;

      Team runnerUp = season.getRunnerUpId() != null
          ? leagueDataService.getTeam(season.getRunnerUpId()) : null;

      try {
        StringBuilder desc = new StringBuilder();
        desc.append("Champion: ").append(champion.getName());
        if (runnerUp != null) {
          desc.append(" | Runner-up: ").append(runnerUp.getName());
        }
        if (season.getTopScorerGoals() > 0) {
          String scorerName = null;
          if (StringUtils.isNotBlank(season.getTopScorerId())) {
            Player p = leagueDataService.getPlayer(season.getTopScorerId());
            if (p != null) {
              scorerName = p.getFirstName() + " " + p.getLastName();
            }
          }
          desc.append(" | Golden Boot: ");
          if (scorerName != null) {
            desc.append(scorerName).append(" (")
                .append(season.getTopScorerGoals()).append(" goals)");
          } else {
            desc.append(season.getTopScorerGoals()).append(" goals");
          }
        }

        KestrosImage crest = null;
        if (champion.getLogoUrl() != null && !champion.getLogoUrl().isEmpty()) {
          try {
            crest = new KestrosImageImpl(
                champion.getLogoUrl(), champion.getName() + " crest", null, null,
                null, null, null, AnchorTarget.SAME_WINDOW,
                this, "image", "imageElement", null);
          } catch (Exception ignored) {}
        }

        cards.add(new KestrosCardImpl(
            desc.toString(),
            new KestrosHeadingImpl(season.getName() + " — " + champion.getName(),
                "h3", this, "title", "titleElement"),
            crest, null, this, "card", "champ-" + season.getId()));
        i++;
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
