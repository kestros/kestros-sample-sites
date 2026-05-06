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
 * Computes the homepage hero subtitle as
 * "Season {name} -- Week {currentMatchday} of {totalMatchweeks}" using the in-progress
 * season and the highest played matchday in that season's fixtures. Avoids hardcoding the
 * matchweek so the homepage stays current as new results land.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonHeroSubtitleTextDataSource extends BaseSlingModelDataSource
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
    Season current = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .orElse(null);
    if (current == null) {
      return "";
    }
    String seasonId = current.getId();
    int currentMatchday = leagueDataService.getMatches().stream()
        .filter(m -> seasonId.equals(m.getSeasonId()))
        .filter(Match::isPlayed)
        .mapToInt(Match::getMatchday)
        .max()
        .orElse(0);
    int totalMatchweeks = leagueDataService.getMatches().stream()
        .filter(m -> seasonId.equals(m.getSeasonId()))
        .mapToInt(Match::getMatchday)
        .max()
        .orElse(0);
    if (totalMatchweeks == 0) {
      return "Season " + current.getName();
    }
    return "Season " + current.getName() + " -- Week " + currentMatchday
        + " of " + totalMatchweeks;
  }
}
