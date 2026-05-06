package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Results page hero subtitle: "{played} matches played across {matchweeks} matchweeks.
 * Most recent first." Counts come from the LeagueDataService so the subtitle stays accurate
 * as the season progresses.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class ResultsCountTextDataSource extends BaseSlingModelDataSource
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
    long played = leagueDataService.getMatches().stream().filter(Match::isPlayed).count();
    long matchweeks = leagueDataService.getMatches().stream()
        .filter(Match::isPlayed)
        .mapToInt(Match::getMatchday)
        .distinct()
        .count();
    return played + " matches played across " + matchweeks
        + " matchweeks. Most recent first.";
  }
}
