package io.kestros.samples.acpl.core.services;

import io.kestros.samples.acpl.api.services.LeagueDataService;
import io.kestros.samples.acpl.api.services.TeamService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Display-ready team data, backed by {@link LeagueDataService}. */
@Component(service = TeamService.class, immediate = true)
public class TeamServiceImpl extends AbstractDisplayService implements TeamService {

  private static final String[][] STAT_TILES = {
      {"p", "Played"}, {"w", "Won"}, {"d", "Drawn"}, {"l", "Lost"},
      {"gf", "GF"}, {"ga", "GA"}, {"gd", "GD"}, {"pts", "Points"}
  };

  @Reference
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public Map<String, String> getTeamHeader(final String club, final String contextPath) {
    final Map<String, String> header = new LinkedHashMap<>();
    final Map<String, Object> c = leagueDataService.getClub(club);
    if (c == null || c.isEmpty()) {
      return header;
    }
    final Map<String, Object> standing = leagueDataService.getStanding(club);
    header.put("slug", club);
    header.put("name", str(c.get("name")));
    header.put("place",
        "Atlantic Coast Premier League — " + ordinal(asInt(standing.get("pos"))) + " Place");
    final StringBuilder meta = new StringBuilder(str(c.get("stadium")));
    if (!str(c.get("mgr")).isEmpty()) {
      meta.append(" · Manager: ").append(str(c.get("mgr")));
    }
    if (!str(c.get("founded")).isEmpty()) {
      meta.append(" · Founded ").append(str(c.get("founded")));
    }
    header.put("meta", meta.toString());
    header.put("base", siteRoot(contextPath));
    return header;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getTeamStatTiles(final String club) {
    final List<Map<String, String>> tiles = new ArrayList<>();
    final Map<String, Object> standing = leagueDataService.getStanding(club);
    if (standing.isEmpty()) {
      return tiles;
    }
    for (final String[] tile : STAT_TILES) {
      final Map<String, String> t = new LinkedHashMap<>();
      t.put("value", str(standing.get(tile[0])));
      t.put("label", tile[1]);
      tiles.add(t);
    }
    return tiles;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getSquadRows(final String club, final String contextPath) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final String base = siteRoot(contextPath);
    for (final Map<String, Object> p : leagueDataService.getSquad(club)) {
      final Map<String, String> row = new LinkedHashMap<>();
      final String name = str(p.get("name"));
      final Object season = p.get("season");
      row.put("num", str(p.get("num")));
      row.put("name", name);
      row.put("pos", str(p.get("pos")));
      row.put("age", str(p.get("age")));
      if (season instanceof Map) {
        final Map<?, ?> s = (Map<?, ?>) season;
        row.put("apps", str(s.get("apps")));
        row.put("goals", str(s.get("goals")));
        row.put("assists", str(s.get("assists")));
      } else {
        row.put("apps", "");
        row.put("goals", "");
        row.put("assists", "");
      }
      row.put("portrait", portraitFor(base, name));
      row.put("href", base + "/players/" + str(p.get("slug")) + ".html");
      row.put("base", base);
      rows.add(row);
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getFormBadges(final String club) {
    final List<Map<String, String>> badges = new ArrayList<>();
    for (final Map<String, Object> f : leagueDataService.getClubForm(club, 5)) {
      final String result = str(f.get("result"));
      final Map<String, String> b = new LinkedHashMap<>();
      b.put("result", result);
      b.put("badgeClass", "W".equals(result) ? "bg-success"
          : "L".equals(result) ? "bg-danger" : "bg-warning");
      b.put("tip", str(f.get("opp")) + " " + str(f.get("gf")) + "-" + str(f.get("ga")));
      badges.add(b);
    }
    return badges;
  }

  @Nonnull
  @Override
  public Map<String, String> getNextFixtureCard(final String club, final String contextPath) {
    final Map<String, String> card = new LinkedHashMap<>();
    final Map<String, Object> fixture = leagueDataService.getNextFixture(club);
    if (fixture.isEmpty()) {
      return card;
    }
    final boolean home = club.equals(str(fixture.get("home")));
    final String oppSlug = str(home ? fixture.get("away") : fixture.get("home"));
    card.put("oppSlug", oppSlug);
    card.put("oppName", leagueDataService.getClubName(oppSlug));
    card.put("meta", (home ? "Home" : "Away") + " · " + formatDateShort(str(fixture.get("date")))
        + " · " + str(fixture.get("kickoff")));
    card.put("base", siteRoot(contextPath));
    card.put("href", siteRoot(contextPath) + "/matches/" + str(fixture.get("id")) + ".html");
    return card;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getTopScorerRows(final String club, final String contextPath) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final String base = siteRoot(contextPath);
    for (final Map<String, Object> p : leagueDataService.getTopScorers(club, 5)) {
      final Map<String, String> row = new LinkedHashMap<>();
      final Object season = p.get("season");
      row.put("name", str(p.get("name")));
      row.put("goals", season instanceof Map ? str(((Map<?, ?>) season).get("goals")) : "0");
      row.put("href", base + "/players/" + str(p.get("slug")) + ".html");
      row.put("base", base);
      rows.add(row);
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getClubDirectoryCards(final String contextPath) {
    final List<Map<String, String>> cards = new ArrayList<>();
    final String base = siteRoot(contextPath);
    for (final Map<String, Object> standing : leagueDataService.getStandings()) {
      final String slug = str(standing.get("club"));
      final Map<String, Object> c = leagueDataService.getClub(slug);
      if (c == null || c.isEmpty()) {
        continue;
      }
      final Map<String, String> card = new LinkedHashMap<>();
      card.put("slug", slug);
      card.put("name", str(c.get("name")));
      card.put("city", str(c.get("city")));
      card.put("stadium", str(c.get("stadium")));
      card.put("mgr", str(c.get("mgr")));
      card.put("pos", ordinal(asInt(standing.get("pos"))));
      card.put("record", standing.get("w") + "-" + standing.get("d") + "-" + standing.get("l"));
      card.put("base", base);
      card.put("href", base + "/teams/" + slug + ".html");
      cards.add(card);
    }
    return cards;
  }
}
