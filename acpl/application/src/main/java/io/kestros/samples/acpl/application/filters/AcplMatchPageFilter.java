package io.kestros.samples.acpl.application.filters;

import io.kestros.cms.sitebuilding.api.filters.DynamicPageFilter;
import io.kestros.cms.sitebuilding.api.models.BaseSite;
import io.kestros.cms.sitebuilding.core.filters.AbstractDynamicPageFilter;
import io.kestros.samples.acpl.api.services.LeagueDataService;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.Filter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Dynamic-page filter for match report pages: {@code /content/sites/<site>/matches/<home>-vs-<away>.html}
 * with no real child page routes to the page claiming {@code acpl-match}, with the match id exposed as
 * the {@code match} request attribute (read by the match datasources). Modeled on
 * {@link AcplTeamPageFilter}.
 *
 * <p>A match that has not been played yet is declined here and handled by
 * {@link AcplMatchPreviewPageFilter}, which dispatches to the page claiming
 * {@code acpl-match-preview}. One filter type is one page, so the two templates are two filters.
 */
@Component(service = {Filter.class, DynamicPageFilter.class})
@SlingServletFilter(scope = SlingServletFilterScope.REQUEST,
    pattern = "/content/sites/.*/matches/.*\\.html",
    methods = "GET")
public class AcplMatchPageFilter extends AbstractDynamicPageFilter {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "ACPL Match Detail";
  }

  @Nonnull
  @Override
  public String getFilterType() {
    return "acpl-match";
  }

  @Nullable
  @Override
  public Map<String, String> extractParameters(final String requestPath, final BaseSite site,
      final SlingHttpServletRequest request) {
    final String suffix = requestPath.substring(site.getResource().getPath().length());
    if (!suffix.startsWith("/matches/")) {
      return null;
    }
    final String matchId = suffix.substring("/matches/".length()).replace(".", "").replace("/", "");
    if (leagueDataService == null) {
      return null;
    }
    final Map<String, Object> match = leagueDataService.getMatch(matchId);
    if (match == null || match.isEmpty()) {
      return null;
    }
    if (!Boolean.TRUE.equals(match.get("played"))) {
      // an unplayed fixture is AcplMatchPreviewPageFilter's; declining passes it down the chain
      return null;
    }
    final String title = leagueDataService.getClubName(String.valueOf(match.get("home"))) + " "
        + match.get("hg") + "–" + match.get("ag") + " "
        + leagueDataService.getClubName(String.valueOf(match.get("away")));
    final Map<String, String> params = new HashMap<>();
    params.put("match", matchId);
    params.put("homeTeam", String.valueOf(match.get("home")));
    params.put("awayTeam", String.valueOf(match.get("away")));
    params.put("dynamicPageTitle", title);
    params.put("dynamicPageDescription", String.valueOf(match.getOrDefault("venue", "")));
    return params;
  }
}
