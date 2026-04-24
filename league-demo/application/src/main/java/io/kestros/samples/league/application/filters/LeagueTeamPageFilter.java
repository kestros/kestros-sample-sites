package io.kestros.samples.league.application.filters;

import io.kestros.cms.sitebuilding.api.models.BaseSite;
import io.kestros.cms.sitebuilding.core.filters.AbstractDynamicPageFilter;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.Filter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component(service = Filter.class)
@SlingServletFilter(scope = SlingServletFilterScope.REQUEST,
    pattern = "/content/sites/.*/teams/.*\\.html",
    methods = "GET")
public class LeagueTeamPageFilter extends AbstractDynamicPageFilter {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "Team Detail";
  }

  @Nonnull
  @Override
  public String getFilterType() {
    return "league-team";
  }

  @Nullable
  @Override
  public Map<String, String> extractParameters(String requestPath, BaseSite site,
      SlingHttpServletRequest request) {
    String sitePath = site.getResource().getPath();
    String suffix = requestPath.substring(sitePath.length());
    if (!suffix.startsWith("/teams/")) {
      return null;
    }
    String slug = suffix.substring("/teams/".length()).replace(".", "").replace("/", "");
    if (leagueDataService == null) {
      return null;
    }
    io.kestros.samples.league.api.models.Team team = leagueDataService.getTeam(slug);
    if (team == null) {
      return null;
    }
    Map<String, String> params = new HashMap<>();
    params.put("team-slug", slug);
    params.put("dynamicPageTitle", team.getName());
    params.put("dynamicPageDescription", team.getCity() + " - " + team.getStadium());
    return params;
  }

  @Nullable
  @Override
  public Resource getTargetPageContent(BaseSite site, SlingHttpServletRequest request) {
    Resource teamPage = site.getResource().getChild("team");
    if (teamPage == null) {
      return null;
    }
    return teamPage.getChild("jcr:content");
  }
}
