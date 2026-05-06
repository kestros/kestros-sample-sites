package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * About page hero subtitle: "{N} seasons. {M} clubs. One champion." Counts from the
 * LeagueDataService so the strapline keeps pace with new seasons and club additions
 * without copy edits.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class AboutHeroSubtitleTextDataSource extends BaseSlingModelDataSource
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
    int seasons = leagueDataService.getSeasons().size();
    int clubs = leagueDataService.getTeams().size();
    return numberWord(seasons) + " seasons. "
        + numberWord(clubs) + " clubs. One champion.";
  }

  private static String numberWord(int n) {
    switch (n) {
      case 1: return "One";
      case 2: return "Two";
      case 3: return "Three";
      case 4: return "Four";
      case 5: return "Five";
      case 6: return "Six";
      case 7: return "Seven";
      case 8: return "Eight";
      case 9: return "Nine";
      case 10: return "Ten";
      case 11: return "Eleven";
      case 12: return "Twelve";
      default: return String.valueOf(n);
    }
  }
}
