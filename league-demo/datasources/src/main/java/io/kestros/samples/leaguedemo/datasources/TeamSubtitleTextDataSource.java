package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamSubtitleTextDataSource extends BaseSlingModelDataSource implements KestrosText {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    if (leagueDataService == null) return "";
    String slug = (String) getRequest().getAttribute("team-slug");
    if (slug == null) return "";
    Team t = leagueDataService.getTeam(slug);
    if (t == null) return "";
    StringBuilder s = new StringBuilder();
    if (t.getNickname() != null && !t.getNickname().isEmpty()) {
      s.append("\"").append(t.getNickname()).append("\"");
    }
    if (t.getCity() != null && !t.getCity().isEmpty()) {
      if (s.length() > 0) s.append(" · ");
      s.append(t.getCity());
    }
    if (t.getFounded() > 0) {
      if (s.length() > 0) s.append(" · ");
      s.append("Founded ").append(t.getFounded());
    }
    if (t.getManager() != null && !t.getManager().isEmpty()) {
      if (s.length() > 0) s.append(" · ");
      s.append("Manager: ").append(t.getManager());
    }
    return s.toString();
  }
}
