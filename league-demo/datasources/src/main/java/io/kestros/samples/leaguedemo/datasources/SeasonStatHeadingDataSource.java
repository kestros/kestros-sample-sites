package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Returns a season-level stat number based on the `metric` property on the heading resource.
 * Supported metrics: matchesPlayed, totalGoals, goalsPerMatch.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonStatHeadingDataSource extends BaseSlingModelDataSource
    implements KestrosHeading {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getHeadingText() {
    if (leagueDataService == null) return "—";
    String metric = getResource().getValueMap().get("metric", String.class);
    if (metric == null) return "—";

    List<Match> played = leagueDataService.getMatches().stream()
        .filter(Match::isPlayed)
        .collect(Collectors.toList());

    switch (metric) {
      case "matchesPlayed":
        return String.valueOf(played.size());
      case "totalGoals": {
        int total = played.stream().mapToInt(m -> m.getHomeScore() + m.getAwayScore()).sum();
        return String.valueOf(total);
      }
      case "goalsPerMatch": {
        if (played.isEmpty()) return "0.00";
        int total = played.stream().mapToInt(m -> m.getHomeScore() + m.getAwayScore()).sum();
        return String.format("%.2f", (double) total / played.size());
      }
      default:
        return "—";
    }
  }

  @Nullable
  @Override
  public String getHeadingType() {
    return getResource().getValueMap().get("level", "h3");
  }
}
