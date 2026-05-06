package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Schedule page intro paragraph. Every quantitative claim is derived from the schedule
 * itself — total matchweeks (max matchday), fixtures per matchweek (teams ÷ 2), and the
 * remaining-fixtures count. Demonstrates that the league's structural facts can be
 * surfaced from data rather than asserted in copy that drifts when teams or weeks change.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class ScheduleIntroTextDataSource extends BaseSlingModelDataSource
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
    List<Match> matches = leagueDataService.getMatches();
    int totalMatchweeks = matches.stream()
        .mapToInt(Match::getMatchday)
        .max()
        .orElse(0);
    int teamCount = leagueDataService.getTeams().size();
    int fixturesPerWeek = teamCount / 2;
    long remaining = matches.stream().filter(m -> !m.isPlayed()).count();
    String seasonName = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .map(Season::getName)
        .orElse("current");

    return "The Meridian Premier League runs over " + totalMatchweeks
        + " matchweeks in a double round-robin format -- every club plays each rival "
        + "twice, once at home and once away. " + NumberWords.words(fixturesPerWeek)
        + " fixtures are played each matchweek so every side is in action on the same "
        + "day. The remaining " + remaining + " fixtures of the " + seasonName
        + " season are listed below.";
  }
}
