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
    pattern = "/content/sites/.*/matches/.*\\.html",
    methods = "GET")
public class LeagueMatchPageFilter extends AbstractDynamicPageFilter {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "Match Detail";
  }

  @Nonnull
  @Override
  public String getFilterType() {
    return "league-match";
  }

  @Nullable
  @Override
  public Map<String, String> extractParameters(String requestPath, BaseSite site,
      SlingHttpServletRequest request) {
    String sitePath = site.getResource().getPath();
    String suffix = requestPath.substring(sitePath.length());
    if (!suffix.startsWith("/matches/")) {
      return null;
    }
    String id = suffix.substring("/matches/".length()).replace(".", "").replace("/", "");
    if (leagueDataService == null) {
      return null;
    }
    io.kestros.samples.league.api.models.Match match = leagueDataService.getMatch(id);
    if (match == null) {
      return null;
    }
    io.kestros.samples.league.api.models.Team home = leagueDataService.getTeam(match.getHomeTeamId());
    io.kestros.samples.league.api.models.Team away = leagueDataService.getTeam(match.getAwayTeamId());
    String homeName = home != null ? home.getName() : match.getHomeTeamId();
    String awayName = away != null ? away.getName() : match.getAwayTeamId();
    String title = homeName + " " + match.getHomeScore() + " - " + match.getAwayScore() + " " + awayName;
    if (!match.isPlayed()) {
      title = homeName + " vs " + awayName;
    }
    Map<String, String> params = new HashMap<>();
    params.put("match-id", id);
    params.put("dynamicPageTitle", title);
    params.put("dynamicPageDescription", "Matchday " + match.getMatchday() + " - " + match.getDate() + " - " + match.getVenue());
    return params;
  }

  @Nullable
  @Override
  public Resource getTargetPageContent(BaseSite site, SlingHttpServletRequest request) {
    Resource page = site.getResource().getChild("match");
    return page != null ? page.getChild("jcr:content") : null;
  }
}
