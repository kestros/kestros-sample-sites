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
    pattern = "/content/sites/.*/seasons/.*\\.html",
    methods = "GET")
public class LeagueSeasonPageFilter extends AbstractDynamicPageFilter {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "Season Detail";
  }

  @Nonnull
  @Override
  public String getFilterType() {
    return "league-season";
  }

  @Nullable
  @Override
  public Map<String, String> extractParameters(String requestPath, BaseSite site,
      SlingHttpServletRequest request) {
    String sitePath = site.getResource().getPath();
    String suffix = requestPath.substring(sitePath.length());
    if (!suffix.startsWith("/seasons/")) {
      return null;
    }
    String id = suffix.substring("/seasons/".length()).replace(".", "").replace("/", "");
    if (leagueDataService == null) {
      return null;
    }
    io.kestros.samples.league.api.models.Season season = leagueDataService.getSeason(id);
    if (season == null) {
      return null;
    }
    Map<String, String> params = new HashMap<>();
    params.put("season-id", id);
    params.put("dynamicPageTitle", season.getName() + " Season");
    params.put("dynamicPageDescription", "Meridian Premier League " + season.getName() + " season");
    return params;
  }

  @Nullable
  @Override
  public Resource getTargetPageContent(BaseSite site, SlingHttpServletRequest request) {
    Resource page = site.getResource().getChild("season");
    return page != null ? page.getChild("jcr:content") : null;
  }
}
