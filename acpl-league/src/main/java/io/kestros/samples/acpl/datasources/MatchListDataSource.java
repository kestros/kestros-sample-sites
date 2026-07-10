package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Match-row list for results and fixtures. A {@code mode} property selects the source:
 * {@code results} (recent, with score) or {@code fixtures} (upcoming, with kickoff). Each match
 * becomes a {@link SyntheticMatchCard} rendered by the {@code result-row} / {@code fixture-row} card
 * layout. A {@code limit} property trims the list for the home widgets.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private String getMode() {
    return getResource().getValueMap().get("mode", "results");
  }

  private int getLimit() {
    return getResource().getValueMap().get("limit", 0);
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) {
      return cards;
    }
    final boolean fixtures = "fixtures".equals(getMode());
    final String layout = fixtures ? "fixture-row" : "result-row";
    final String base = siteRoot();
    final String matchHref = base + "/match.html";
    final List<Map<String, Object>> matches = fixtures
        ? leagueDataService.getUpcomingFixtures()
        : leagueDataService.getRecentResults();
    final int limit = getLimit();
    int i = 0;
    for (final Map<String, Object> m : matches) {
      if (limit > 0 && i >= limit) {
        break;
      }
      try {
        final String home = str(m.get("home"));
        final String away = str(m.get("away"));
        final String mid = fixtures
            ? str(m.get("day")) + " " + str(m.get("kickoff"))
            : str(m.get("hg")) + " – " + str(m.get("ag"));
        cards.add(new SyntheticMatchCard(layout, base, matchHref,
            home, leagueDataService.getClubShort(home), leagueDataService.getClubName(home),
            away, leagueDataService.getClubShort(away), leagueDataService.getClubName(away),
            mid, this, "card", "match-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe: skip a match that fails to build
      }
    }
    return cards;
  }

  /** League-site root (e.g. {@code /content/sites/acpl}), from any hosting page. */
  private String siteRoot() {
    final Matcher m = Pattern.compile("^(/content/sites/[^/]+)").matcher(getResource().getPath());
    return m.find() ? m.group(1) : getResource().getPath();
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
