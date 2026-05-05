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

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonRecentIntroTextDataSource extends BaseSlingModelDataSource
    implements KestrosText {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    if (leagueDataService == null) return "";
    String seasonId = (String) getRequest().getAttribute("season-id");
    if (seasonId == null) return "";
    long count = leagueDataService.getMatches().stream()
        .filter(m -> seasonId.equals(m.getSeasonId()))
        .filter(Match::isPlayed)
        .count();
    if (count == 0) {
      return "Match-by-match data isn't archived for this season -- only the season summary above is available.";
    }
    long shown = Math.min(count, 10);
    return "The " + shown + " most recent results from this season.";
  }
}
