package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamTitleHeadingDataSource extends BaseSlingModelDataSource
    implements KestrosHeading {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getHeadingText() {
    if (leagueDataService == null) return "Team";
    String slug = (String) getRequest().getAttribute("team-slug");
    if (slug == null) return "Team";
    Team team = leagueDataService.getTeam(slug);
    return team != null ? team.getName() : slug;
  }

  @Nullable
  @Override
  public String getHeadingType() {
    String level = getResource().getValueMap().get("level", "h1");
    return level;
  }
}
