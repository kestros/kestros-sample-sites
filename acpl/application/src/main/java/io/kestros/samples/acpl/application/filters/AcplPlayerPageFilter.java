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
 * Dynamic-page filter for player detail pages: {@code /content/sites/<site>/players/<slug>.html} with
 * no real child page routes to the shared {@code player} template, with the player slug exposed as the
 * {@code player} request attribute (read by the player datasources). Modeled on {@link AcplTeamPageFilter}.
 */
@Component(service = Filter.class)
@SlingServletFilter(scope = SlingServletFilterScope.REQUEST,
    pattern = "/content/sites/.*/players/.*\\.html",
    methods = "GET")
public class AcplPlayerPageFilter extends AbstractDynamicPageFilter {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "ACPL Player Detail";
  }

  @Nonnull
  @Override
  public String getFilterType() {
    return "acpl-player";
  }

  @Nullable
  @Override
  public Map<String, String> extractParameters(final String requestPath, final BaseSite site,
      final SlingHttpServletRequest request) {
    final String suffix = requestPath.substring(site.getResource().getPath().length());
    if (!suffix.startsWith("/players/")) {
      return null;
    }
    final String slug = suffix.substring("/players/".length()).replace(".", "").replace("/", "");
    if (leagueDataService == null) {
      return null;
    }
    final Map<String, Object> player = leagueDataService.getPlayer(slug);
    if (player == null || player.isEmpty()) {
      return null;
    }
    final Map<String, String> params = new HashMap<>();
    params.put("player", slug);
    params.put("dynamicPageTitle", String.valueOf(player.getOrDefault("name", slug)));
    params.put("dynamicPageDescription",
        String.valueOf(leagueDataService.getClubName(String.valueOf(player.get("club")))));
    return params;
  }

  @Nullable
  @Override
  public Resource getTargetPageContent(final BaseSite site, final SlingHttpServletRequest request) {
    final Resource playerPage = site.getResource().getChild("player");
    if (playerPage == null) {
      return null;
    }
    return playerPage.getChild("jcr:content");
  }
}
