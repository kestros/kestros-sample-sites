package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonNameHeadingDataSource extends BaseSlingModelDataSource
    implements KestrosHeading {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getHeadingText() {
    if (leagueDataService == null) return "Season";
    String slug = (String) getRequest().getAttribute("season-id");
    if (slug == null) return "Season";
    Season season = leagueDataService.getSeason(slug);
    return season != null ? season.getName() + " Season" : slug;
  }

  @Nullable
  @Override
  public String getHeadingType() {
    return getResource().getValueMap().get("level", "h1");
  }
}
