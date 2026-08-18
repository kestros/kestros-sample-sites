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
 * Dynamic-page filter for match detail pages: {@code /content/sites/<site>/matches/<home>-vs-<away>.html}
 * with no real child page routes to the shared {@code match} template, with the match id exposed as the
 * {@code match} request attribute (read by the match datasources). Modeled on {@link AcplTeamPageFilter}.
 */
@Component(service = Filter.class)
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
    final boolean played = Boolean.TRUE.equals(match.get("played"));
    final String title;
    if (played) {
      title = leagueDataService.getClubName(String.valueOf(match.get("home"))) + " "
          + match.get("hg") + "–" + match.get("ag") + " "
          + leagueDataService.getClubName(String.valueOf(match.get("away")));
    } else {
      title = leagueDataService.getClubName(String.valueOf(match.get("home"))) + " vs "
          + leagueDataService.getClubName(String.valueOf(match.get("away")));
    }
    // read by getTargetPageContent to route played matches to the report template and
    // unplayed ones to the preview template (extractParameters runs first)
    request.setAttribute("acplMatchPlayed", played);
    final Map<String, String> params = new HashMap<>();
    params.put("match", matchId);
    params.put("homeTeam", String.valueOf(match.get("home")));
    params.put("awayTeam", String.valueOf(match.get("away")));
    params.put("dynamicPageTitle", title);
    params.put("dynamicPageDescription", String.valueOf(match.getOrDefault("venue", "")));
    return params;
  }

  @Nullable
  @Override
  public Resource getTargetPageContent(final BaseSite site, final SlingHttpServletRequest request) {
    final boolean played = Boolean.TRUE.equals(request.getAttribute("acplMatchPlayed"));
    Resource matchPage = site.getResource().getChild(played ? "match" : "match-preview");
    if (matchPage == null) {
      // fall back to the report template if the preview template is missing
      matchPage = site.getResource().getChild("match");
    }
    if (matchPage == null) {
      return null;
    }
    return matchPage.getChild("jcr:content");
  }
}
