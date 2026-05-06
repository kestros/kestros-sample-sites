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
 * Schedule page intro paragraph: dynamic count of remaining fixtures and current season name,
 * appended to the static league-format description. Demonstrates how authoring-friendly text
 * can be combined with live data without splitting the paragraph into separate components.
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
    long remaining = leagueDataService.getMatches().stream()
        .filter(m -> !m.isPlayed())
        .count();
    String seasonName = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .map(Season::getName)
        .orElse("current");

    return "The Meridian Premier League runs an 18-matchweek double round-robin format -- "
        + "every club plays each rival twice, once at home and once away. Five fixtures "
        + "are played each matchweek so every side is in action on the same day. The "
        + "remaining " + remaining + " fixtures of the " + seasonName + " season are "
        + "listed below.";
  }
}
