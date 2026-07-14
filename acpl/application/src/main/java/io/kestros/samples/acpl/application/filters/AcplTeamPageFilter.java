package io.kestros.samples.acpl.application.filters;

import io.kestros.cms.sitebuilding.api.models.BaseSite;
import io.kestros.cms.sitebuilding.core.filters.AbstractDynamicPageFilter;
import io.kestros.samples.acpl.api.services.LeagueDataService;
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

/**
 * Dynamic-page filter for team detail pages: {@code /content/sites/<site>/teams/<slug>.html} with no real
 * child page routes to the shared {@code team} template, with the club slug exposed as the {@code team}
 * request attribute (read by the team datasources). Modeled on league-demo's LeagueTeamPageFilter.
 */
@Component(service = Filter.class)
@SlingServletFilter(scope = SlingServletFilterScope.REQUEST,
    pattern = "/content/sites/.*/teams/.*\\.html",
    methods = "GET")
public class AcplTeamPageFilter extends AbstractDynamicPageFilter {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "ACPL Team Detail";
  }

  @Nonnull
  @Override
  public String getFilterType() {
    return "acpl-team";
  }

  @Nullable
  @Override
  public Map<String, String> extractParameters(final String requestPath, final BaseSite site,
      final SlingHttpServletRequest request) {
    final String suffix = requestPath.substring(site.getResource().getPath().length());
    if (!suffix.startsWith("/teams/")) {
      return null;
    }
    final String slug = suffix.substring("/teams/".length()).replace(".", "").replace("/", "");
    if (leagueDataService == null) {
      return null;
    }
    final Map<String, Object> club = leagueDataService.getClub(slug);
    if (club == null || club.isEmpty()) {
      return null;
    }
    final Map<String, String> params = new HashMap<>();
    params.put("team", slug);
    params.put("dynamicPageTitle", String.valueOf(club.getOrDefault("name", slug)));
    params.put("dynamicPageDescription",
        String.valueOf(club.getOrDefault("stadium", "")));
    return params;
  }

  @Nullable
  @Override
  public Resource getTargetPageContent(final BaseSite site, final SlingHttpServletRequest request) {
    final Resource teamPage = site.getResource().getChild("team");
    if (teamPage == null) {
      return null;
    }
    return teamPage.getChild("jcr:content");
  }
}
