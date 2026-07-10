package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.services.LeagueDataService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Matches grouped by matchweek — one card per week with a "Matchweek N" heading. {@code mode} selects
 * results (recent, newest week first, with score + date) or fixtures (upcoming, soonest week first,
 * with kickoff). Each group is a {@link SyntheticMatchweekGroup} rendered by the
 * {@code results-group} / {@code fixtures-group} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class GroupedMatchListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private String getMode() {
    return getResource().getValueMap().get("mode", "results");
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> groups = new ArrayList<>();
    if (leagueDataService == null) {
      return groups;
    }
    final boolean fixtures = "fixtures".equals(getMode());
    final String layout = fixtures ? "fixtures-group" : "results-group";
    final String base = siteRoot();
    final String matchHref = base + "/match.html";

    // record (W-D-L) per club from the standings
    final Map<String, String> records = new java.util.HashMap<>();
    for (final Map<String, Object> s : leagueDataService.getStandings()) {
      records.put(str(s.get("club")),
          "(" + str(s.get("w")) + "-" + str(s.get("d")) + "-" + str(s.get("l")) + ")");
    }

    // group by matchweek; TreeMap keeps weeks ordered, reversed for results (newest first)
    final TreeMap<Integer, List<Map<String, String>>> byWeek = new TreeMap<>();
    final List<Map<String, Object>> matches = fixtures
        ? leagueDataService.getUpcomingFixtures()
        : leagueDataService.getRecentResults();
    for (final Map<String, Object> m : matches) {
      final int mw = toInt(m.get("mw"));
      final String home = str(m.get("home"));
      final String away = str(m.get("away"));
      final Map<String, String> row = new LinkedHashMap<>();
      row.put("homeSlug", home);
      row.put("homeName", leagueDataService.getClubName(home));
      row.put("homeRec", records.getOrDefault(home, ""));
      row.put("awaySlug", away);
      row.put("awayName", leagueDataService.getClubName(away));
      row.put("awayRec", records.getOrDefault(away, ""));
      if (fixtures) {
        row.put("time", str(m.get("kickoff")));
      } else {
        row.put("score", str(m.get("hg")) + " – " + str(m.get("ag")));
        row.put("date", formatDate(str(m.get("date"))));
      }
      byWeek.computeIfAbsent(mw, k -> new ArrayList<>()).add(row);
    }

    final List<Integer> weeks = new ArrayList<>(byWeek.keySet());
    if (!fixtures) {
      java.util.Collections.reverse(weeks); // results: newest matchweek first
    }
    int i = 0;
    for (final Integer week : weeks) {
      try {
        groups.add(new SyntheticMatchweekGroup(layout, "Matchweek " + week, base, matchHref,
            byWeek.get(week), this, "card", "mw-" + week));
        i++;
      } catch (final Exception e) {
        // null-safe: skip a group that fails to build
      }
    }
    return groups;
  }

  /** League-site root (e.g. {@code /content/sites/acpl}), from any hosting page. */
  private String siteRoot() {
    final Matcher m = Pattern.compile("^(/content/sites/[^/]+)").matcher(getResource().getPath());
    return m.find() ? m.group(1) : getResource().getPath();
  }

  /** {@code 2026-02-07} → {@code Sat, 7 Feb 2026}; input unchanged if unparseable. */
  private static String formatDate(final String iso) {
    try {
      return java.time.LocalDate.parse(iso)
          .format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM yyyy",
              java.util.Locale.ENGLISH));
    } catch (final Exception e) {
      return iso;
    }
  }

  private static int toInt(final Object o) {
    try {
      return Integer.parseInt(String.valueOf(o));
    } catch (final Exception e) {
      return 0;
    }
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
