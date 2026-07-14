package io.kestros.samples.acpl.core.services;

import io.kestros.samples.acpl.api.services.LeagueDataService;
import io.kestros.samples.acpl.api.services.ScheduleService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Display-ready results/fixtures data, backed by {@link LeagueDataService}. */
@Component(service = ScheduleService.class, immediate = true)
public class ScheduleServiceImpl extends AbstractDisplayService implements ScheduleService {

  @Reference
  private LeagueDataService leagueDataService;

  /** W-D-L record strings per club, from the standings. */
  private Map<String, String> records() {
    final Map<String, String> records = new LinkedHashMap<>();
    for (final Map<String, Object> s : leagueDataService.getStandings()) {
      records.put(str(s.get("club")),
          "(" + str(s.get("w")) + "-" + str(s.get("d")) + "-" + str(s.get("l")) + ")");
    }
    return records;
  }

  /** Compact display name for narrow viewports: full name minus the club-type suffix. */
  private String shortDisplay(final String slug) {
    final String name = leagueDataService.getClubName(slug);
    final int i = name.lastIndexOf(' ');
    if (i > 0) {
      final String last = name.substring(i + 1);
      if ("FC".equals(last) || "United".equals(last) || "Athletic".equals(last)
          || "Town".equals(last) || "City".equals(last) || "Rovers".equals(last)
          || "Wanderers".equals(last) || "Park".equals(last)) {
        return name.substring(0, i);
      }
    }
    return name;
  }

  private List<Map<String, Object>> grouped(final List<Map<String, Object>> matches,
      final boolean fixtures, final String contextPath) {
    final String base = siteRoot(contextPath);
    final Map<String, String> records = records();
    final TreeMap<Integer, List<Map<String, String>>> byWeek = new TreeMap<>();
    for (final Map<String, Object> m : matches) {
      final String home = str(m.get("home"));
      final String away = str(m.get("away"));
      final Map<String, String> row = new LinkedHashMap<>();
      row.put("homeSlug", home);
      row.put("homeShort", shortDisplay(home));
      row.put("homeName", leagueDataService.getClubName(home));
      row.put("homeRec", fixtures ? records.getOrDefault(home, "") : "");
      row.put("awaySlug", away);
      row.put("awayShort", shortDisplay(away));
      row.put("awayName", leagueDataService.getClubName(away));
      row.put("awayRec", fixtures ? records.getOrDefault(away, "") : "");
      row.put("href", base + "/matches/" + str(m.get("id")) + ".html");
      if (fixtures) {
        row.put("time", str(m.get("kickoff")));
      } else {
        row.put("score", str(m.get("hg")) + " – " + str(m.get("ag")));
        row.put("date", formatDateResult(str(m.get("date"))));
      }
      byWeek.computeIfAbsent(asInt(m.get("mw")), k -> new ArrayList<>()).add(row);
    }
    final List<Map<String, Object>> groups = new ArrayList<>();
    for (final Map.Entry<Integer, List<Map<String, String>>> e : byWeek.entrySet()) {
      final Map<String, Object> group = new LinkedHashMap<>();
      group.put("matchweek", "Matchweek " + e.getKey());
      group.put("base", base);
      group.put("matches", e.getValue());
      groups.add(group);
    }
    return groups;
  }

  @Nonnull
  @Override
  public List<Map<String, Object>> getResultsByMatchweek(final String contextPath) {
    return grouped(leagueDataService.getPlayedMatches(), false, contextPath);
  }

  @Nonnull
  @Override
  public List<Map<String, Object>> getFixturesByMatchweek(final String contextPath) {
    return grouped(leagueDataService.getUpcomingFixtures(), true, contextPath);
  }

  private List<Map<String, String>> widgetRows(final List<Map<String, Object>> matches,
      final boolean fixtures, final String contextPath, final int limit) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final String base = siteRoot(contextPath);
    // Whole-matchweek integrity: the widget covers the newest (results) / next (fixtures) matchweek
    // completely — a limit smaller than the matchweek must never hide a game (a 12-team league plays
    // 6 per week; hiding one misleads).
    Integer widgetWeek = null;
    if (!matches.isEmpty()) {
      widgetWeek = asInt((fixtures ? matches.get(0) : matches.get(0)).get("mw"));
    }
    for (final Map<String, Object> m : matches) {
      if (widgetWeek != null && asInt(m.get("mw")) != widgetWeek) {
        break;
      }
      if (limit > 0 && rows.size() >= limit) {
        break;
      }
      final String home = str(m.get("home"));
      final String away = str(m.get("away"));
      final Map<String, String> row = new LinkedHashMap<>();
      row.put("homeSlug", home);
      row.put("homeShort", shortDisplay(home));
      row.put("homeName", leagueDataService.getClubName(home));
      row.put("awaySlug", away);
      row.put("awayShort", shortDisplay(away));
      row.put("awayName", leagueDataService.getClubName(away));
      row.put("mid", fixtures
          ? str(m.get("day")) + " " + str(m.get("kickoff"))
          : str(m.get("hg")) + " – " + str(m.get("ag")));
      row.put("href", base + "/matches/" + str(m.get("id")) + ".html");
      row.put("base", base);
      rows.add(row);
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getRecentResultRows(final String contextPath, final int limit) {
    return widgetRows(leagueDataService.getRecentResults(), false, contextPath, limit);
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getUpcomingFixtureRows(final String contextPath, final int limit) {
    return widgetRows(leagueDataService.getUpcomingFixtures(), true, contextPath, limit);
  }

  @Nonnull
  @Override
  public List<String> getMatchweekOptions(final String mode) {
    final TreeSet<Integer> weeks = new TreeSet<>();
    final List<Map<String, Object>> source = "fixtures".equals(mode)
        ? leagueDataService.getUpcomingFixtures()
        : leagueDataService.getPlayedMatches();
    for (final Map<String, Object> m : source) {
      weeks.add(asInt(m.get("mw")));
    }
    weeks.remove(0);
    final List<String> out = new ArrayList<>();
    for (final Integer w : weeks) {
      out.add(String.valueOf(w));
    }
    return out;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getClubOptions() {
    final List<Map<String, String>> clubs = new ArrayList<>();
    for (final Map<String, Object> c : leagueDataService.getClubs()) {
      final Map<String, String> option = new LinkedHashMap<>();
      option.put("slug", str(c.get("slug")));
      option.put("name", str(c.get("name")));
      clubs.add(option);
    }
    return clubs;
  }
}
