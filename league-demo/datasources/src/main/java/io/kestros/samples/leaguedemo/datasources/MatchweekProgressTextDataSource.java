package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
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
 * Generic matchweek-progress text. Reads a {@code prefix} property from the resource
 * and emits "{prefix} {N}" where N is the highest played matchday in the in-progress
 * season. Used for page subtitles like "Updated through Matchweek 12" or
 * "2025-26 season leaders through Matchweek 12" where only the prefix differs.
 *
 * <p>If no {@code prefix} is set, defaults to "Updated through Matchweek".
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchweekProgressTextDataSource extends BaseSlingModelDataSource
    implements KestrosText {

  private static final String DEFAULT_PREFIX = "Updated through Matchweek";

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
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
    int currentMatchday = leagueDataService.getMatches().stream()
        .filter(m -> current.getId().equals(m.getSeasonId()))
        .filter(Match::isPlayed)
        .mapToInt(Match::getMatchday)
        .max()
        .orElse(0);
    String prefix = getResource().getValueMap().get("prefix", DEFAULT_PREFIX);
    return prefix + " " + currentMatchday;
  }
}
