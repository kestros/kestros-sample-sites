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
 * About page "Competition Format" paragraph — every quantitative claim (matchweeks,
 * opponents, matches per team, total fixtures) is computed from team and match data
 * rather than hand-asserted. Demonstrates how a league's structural description can be
 * surfaced from the source of truth so the about page never goes out of sync with a
 * change to the schedule or an expansion in club count.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class CompetitionFormatTextDataSource extends BaseSlingModelDataSource
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
    int teams = leagueDataService.getTeams().size();
    int totalMatchweeks = leagueDataService.getMatches().stream()
        .mapToInt(Match::getMatchday)
        .max()
        .orElse(0);
    int opponents = Math.max(0, teams - 1);
    int matchesPerTeam = opponents * 2;
    int totalMatches = leagueDataService.getMatches().size();

    return "The MPL follows a double round-robin format across " + totalMatchweeks
        + " matchweeks. Every club plays each of the other "
        + NumberWords.wordsLower(opponents) + " opponents twice -- once at home and "
        + "once away -- for a total of " + matchesPerTeam + " league matches per team "
        + "and " + totalMatches + " matches per season.";
  }
}
