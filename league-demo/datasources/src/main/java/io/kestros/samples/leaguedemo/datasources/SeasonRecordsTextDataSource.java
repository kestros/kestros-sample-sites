package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Generic season-records text. Reads a {@code metric} property from the resource and
 * returns the corresponding all-time record sentence. Supported metrics:
 *
 * <ul>
 *   <li>{@code most-titles} — the team with the most championships, with the years won.</li>
 *   <li>{@code most-runners-up} — the team with the most runner-up finishes.</li>
 *   <li>{@code highest-single-season-goals} — the season-record top-scorer line.</li>
 * </ul>
 *
 * <p>Demonstrates how a single configurable component can replace several hand-edited
 * facts that would otherwise need updating after every season's results.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonRecordsTextDataSource extends BaseSlingModelDataSource
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
    String metric = getResource().getValueMap().get("metric", "most-titles");
    switch (metric) {
      case "most-titles":
        return mostByField(Season::getChampionId, "champion");
      case "most-runners-up":
        return mostByField(Season::getRunnerUpId, "runner-up");
      case "highest-single-season-goals":
        return highestSingleSeasonGoals();
      default:
        return "";
    }
  }

  private String mostByField(java.util.function.Function<Season, String> field, String role) {
    Map<String, List<Season>> bucket = new HashMap<>();
    for (Season s : leagueDataService.getSeasons()) {
      String teamId = field.apply(s);
      if (StringUtils.isBlank(teamId)) continue;
      bucket.computeIfAbsent(teamId, k -> new ArrayList<>()).add(s);
    }
    if (bucket.isEmpty()) return "—";
    Map.Entry<String, List<Season>> top = bucket.entrySet().stream()
        .max(Comparator.comparingInt(e -> e.getValue().size()))
        .orElse(null);
    if (top == null) return "—";
    Team team = leagueDataService.getTeam(top.getKey());
    String teamName = team != null ? team.getName() : top.getKey();
    String years = top.getValue().stream()
        .map(Season::getName)
        .sorted()
        .collect(java.util.stream.Collectors.joining(", "));
    int count = top.getValue().size();
    return teamName + " (" + count + ") -- "
        + (count == 1 ? "the lone " + role + " in " : "")
        + years + (count == 1 ? "." : "");
  }

  private String highestSingleSeasonGoals() {
    Season top = leagueDataService.getSeasons().stream()
        .filter(s -> s.getTopScorerGoals() > 0
            && StringUtils.isNotBlank(s.getTopScorerId()))
        .max(Comparator.comparingInt(Season::getTopScorerGoals))
        .orElse(null);
    if (top == null) return "—";
    Player p = leagueDataService.getPlayer(top.getTopScorerId());
    String name = p != null
        ? p.getFirstName() + " " + p.getLastName()
        : top.getTopScorerId();
    return name + " -- " + top.getTopScorerGoals() + " goals (" + top.getName()
        + "). The all-time single-season record.";
  }
}
