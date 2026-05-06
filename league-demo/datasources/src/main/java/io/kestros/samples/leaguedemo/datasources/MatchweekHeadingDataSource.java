package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Heading datasource for matchweek-relative labels like "Week 12 Results" or
 * "Week 13 Fixtures". Reads two properties from the resource:
 *
 * <ul>
 *   <li>{@code mode} — {@code current} for the most recent played matchweek (results),
 *       {@code next} for the next upcoming matchweek (fixtures). Defaults to {@code current}.</li>
 *   <li>{@code suffix} — text appended after the week number (e.g. "Results", "Fixtures").
 *       Defaults to "Results".</li>
 * </ul>
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchweekHeadingDataSource extends BaseSlingModelDataSource
    implements KestrosHeading {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getHeadingText() {
    if (leagueDataService == null) {
      return "";
    }
    Season current = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .orElse(null);
    if (current == null) {
      return "";
    }
    String mode = getResource().getValueMap().get("mode", "current");
    String suffix = getResource().getValueMap().get("suffix", "Results");
    int currentMatchday = leagueDataService.getMatches().stream()
        .filter(m -> current.getId().equals(m.getSeasonId()))
        .filter(Match::isPlayed)
        .mapToInt(Match::getMatchday)
        .max()
        .orElse(0);
    int week = "next".equals(mode) ? currentMatchday + 1 : currentMatchday;
    return "Week " + week + " " + suffix;
  }

  @Nullable
  @Override
  public String getHeadingType() {
    return getResource().getValueMap().get("level", "h3");
  }
}
