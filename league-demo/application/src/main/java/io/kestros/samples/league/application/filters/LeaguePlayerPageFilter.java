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
    pattern = "/content/sites/.*/players/.*\\.html",
    methods = "GET")
public class LeaguePlayerPageFilter extends AbstractDynamicPageFilter {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "Player Detail";
  }

  @Nonnull
  @Override
  public String getFilterType() {
    return "league-player";
  }

  @Nullable
  @Override
  public Map<String, String> extractParameters(String requestPath, BaseSite site,
      SlingHttpServletRequest request) {
    String sitePath = site.getResource().getPath();
    String suffix = requestPath.substring(sitePath.length());
    if (!suffix.startsWith("/players/")) {
      return null;
    }
    String slug = suffix.substring("/players/".length()).replace(".", "").replace("/", "");
    if (leagueDataService == null) {
      return null;
    }
    io.kestros.samples.league.api.models.Player player = leagueDataService.getPlayer(slug);
    if (player == null) {
      return null;
    }
    io.kestros.samples.league.api.models.Team team = leagueDataService.getTeam(player.getTeamId());
    String teamName = team != null ? team.getName() : "";
    Map<String, String> params = new HashMap<>();
    params.put("player-slug", slug);
    params.put("dynamicPageTitle", player.getFirstName() + " " + player.getLastName());
    params.put("dynamicPageDescription", player.getPosition() + " #" + player.getNumber() + " for " + teamName);
    return params;
  }

  @Nullable
  @Override
  public Resource getTargetPageContent(BaseSite site, SlingHttpServletRequest request) {
    Resource page = site.getResource().getChild("player");
    return page != null ? page.getChild("jcr:content") : null;
  }
}
