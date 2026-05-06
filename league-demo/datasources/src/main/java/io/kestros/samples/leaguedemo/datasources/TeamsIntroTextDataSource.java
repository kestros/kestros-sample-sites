package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Teams page intro paragraph: dynamically composed from team count, in-progress season,
 * and the league's oldest and youngest clubs (by founding year). Demonstrates that even
 * narrative copy can be data-driven instead of hand-written and re-edited each season.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamsIntroTextDataSource extends BaseSlingModelDataSource
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

    List<Team> teams = leagueDataService.getTeams();
    if (teams.isEmpty()) {
      return "";
    }

    Optional<Team> oldest = teams.stream()
        .filter(t -> t.getFounded() > 0)
        .min(Comparator.comparingInt(Team::getFounded));
    Optional<Team> youngest = teams.stream()
        .filter(t -> t.getFounded() > 0)
        .max(Comparator.comparingInt(Team::getFounded));

    String seasonName = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .map(Season::getName)
        .orElse("current");

    StringBuilder sb = new StringBuilder("The Meridian Premier League brings together ");
    sb.append(teams.size()).append(" clubs from across the region");
    if (oldest.isPresent() && youngest.isPresent() && !oldest.get().getId().equals(youngest.get().getId())) {
      sb.append(" -- from ")
          .append(oldest.get().getName())
          .append(", founded in ")
          .append(oldest.get().getFounded())
          .append(" as the league's oldest, to ")
          .append(youngest.get().getName())
          .append(", established in ")
          .append(youngest.get().getFounded())
          .append('.');
    } else {
      sb.append('.');
    }
    sb.append(" Each one carries its own colours, traditions, and rivalries into every "
        + "matchweek of the ").append(seasonName).append(" season.");
    return sb.toString();
  }
}
