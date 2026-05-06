package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Generic matchweek-progress text. Emits "{prefix} {N}{suffix}" where N is the highest played
 * matchday in the in-progress season, optionally shifted by {@code offset} (e.g. +1 to refer to
 * the next matchweek).
 *
 * <p>Reads three properties from the resource:
 * <ul>
 *   <li>{@code prefix} — text before the number. Defaults to "Updated through Matchweek".</li>
 *   <li>{@code suffix} — text after the number (commonly empty or a sentence continuation
 *       like ", plus the fixtures coming this weekend."). Defaults to "".</li>
 *   <li>{@code offset} — integer added to the current matchday. Defaults to 0.</li>
 * </ul>
 *
 * <p>Both {@code prefix} and {@code suffix} support the placeholder
 * {@code ${seasonName}}, which is replaced with the in-progress season's display name
 * (e.g. "2025-26"). This lets pages reference the season without hardcoding the year.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchweekProgressTextDataSource extends BaseSlingModelDataSource
    implements KestrosText {

  private static final String DEFAULT_PREFIX = "Updated through Matchweek";
  private static final String SEASON_NAME_TOKEN = "${seasonName}";

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    if (leagueDataService == null) {
      return "";
    }
    Season current = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .orElse(null);
    if (current == null) {
      return "";
    }
    int currentMatchday = leagueDataService.getMatches().stream()
        .filter(m -> current.getId().equals(m.getSeasonId()))
        .filter(Match::isPlayed)
        .mapToInt(Match::getMatchday)
        .max()
        .orElse(0);
    String seasonName = current.getName() != null ? current.getName() : "";
    String prefix = getResource().getValueMap().get("prefix", DEFAULT_PREFIX)
        .replace(SEASON_NAME_TOKEN, seasonName);
    String suffix = getResource().getValueMap().get("suffix", "")
        .replace(SEASON_NAME_TOKEN, seasonName);
    int offset = getResource().getValueMap().get("offset", 0);
    return prefix + " " + (currentMatchday + offset) + suffix;
  }
}
