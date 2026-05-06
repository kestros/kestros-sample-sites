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
    return NumberWords.words(seasons) + " seasons. "
        + NumberWords.words(clubs) + " clubs. One champion.";
  }
}
