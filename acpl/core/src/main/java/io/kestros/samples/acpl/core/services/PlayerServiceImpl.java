package io.kestros.samples.acpl.core.services;

import io.kestros.samples.acpl.api.services.LeagueDataService;
import io.kestros.samples.acpl.api.services.PlayerService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Display-ready player data, backed by {@link LeagueDataService}. */
@Component(service = PlayerService.class, immediate = true)
public class PlayerServiceImpl extends AbstractDisplayService implements PlayerService {

  private static final String[][] STAT_TILES = {
      {"apps", "Appearances"}, {"goals", "Goals"}, {"assists", "Assists"},
      {"minutes", "Minutes"}, {"yellows", "Yellows"}, {"reds", "Reds"}
  };

  /** Goalkeepers: goals/assists tiles are noise — show the applicable ones. */
  private static final String[][] GK_STAT_TILES = {
      {"apps", "Appearances"}, {"minutes", "Minutes"},
      {"yellows", "Yellows"}, {"reds", "Reds"}
  };

  @Reference
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public Map<String, String> getPlayerHeader(final String slug, final String contextPath) {
    final Map<String, String> header = new LinkedHashMap<>();
    final Map<String, Object> player = leagueDataService.getPlayer(slug);
    if (player == null || player.isEmpty()) {
      return header;
    }
    final String name = str(player.get("name"));
    header.put("name", name);
    header.put("club", leagueDataService.getClubName(str(player.get("club"))));
    header.put("clubHref",
        siteRoot(contextPath) + "/teams/" + str(player.get("club")) + ".html");
    header.put("number", "#" + str(player.get("num")));
    header.put("pos", str(player.get("pos")));
    final StringBuilder meta = new StringBuilder(str(player.get("pos")));
    if (!str(player.get("age")).isEmpty()) {
      meta.append(" · Age ").append(str(player.get("age")));
    }
    if (!str(player.get("height")).isEmpty()) {
      meta.append(" · ").append(str(player.get("height")));
    }
    if (!str(player.get("nationality")).isEmpty()) {
      meta.append(" · ").append(str(player.get("nationality")));
    }
    header.put("meta", meta.toString());
    header.put("photo", portraitFor(siteRoot(contextPath), name));
    header.put("base", siteRoot(contextPath));
    return header;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getPlayerStatTiles(final String slug) {
    final List<Map<String, String>> tiles = new ArrayList<>();
    final Map<String, Object> player = leagueDataService.getPlayer(slug);
    if (player == null || !(player.get("season") instanceof Map)) {
      return tiles;
    }
    final Map<?, ?> season = (Map<?, ?>) player.get("season");
    final String[][] tileDefs = "GK".equals(str(player.get("pos"))) ? GK_STAT_TILES : STAT_TILES;
    for (final String[] tile : tileDefs) {
      final Map<String, String> t = new LinkedHashMap<>();
      t.put("value", str(season.get(tile[0])));
      t.put("label", tile[1]);
      tiles.add(t);
    }
    return tiles;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public List<Map<String, String>> getLast5Rows(final String slug, final String contextPath) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final Object l5 = leagueDataService.getPlayer(slug).get("last5");
    if (!(l5 instanceof List)) {
      return rows;
    }
    final String base = siteRoot(contextPath);
    for (final Map<String, Object> m : (List<Map<String, Object>>) l5) {
      final Map<String, String> row = new LinkedHashMap<>();
      row.put("mw", "MW" + str(m.get("mw")));
      row.put("opp", leagueDataService.getClubName(str(m.get("opp"))));
      row.put("oppSlug", str(m.get("opp")));
      row.put("venue", str(m.get("venue")));
      row.put("res", str(m.get("res")));
      row.put("g", str(m.get("g")));
      row.put("a", str(m.get("a")));
      row.put("rating", str(m.get("rating")));
      row.put("href", base + "/matches/" + str(m.get("id")) + ".html");
      row.put("base", base);
      // newest first — source data is oldest-to-newest
      rows.add(0, row);
    }
    return rows;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public List<Map<String, String>> getLeagueLeaders(final String stat, final int limit,
      final String contextPath) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final String base = siteRoot(contextPath);
    final List<Map<String, Object>> players = new ArrayList<>(leagueDataService.getPlayers());
    players.sort((a, b) -> Integer.compare(seasonStat(b, stat), seasonStat(a, stat)));
    for (final Map<String, Object> p : players) {
      if (rows.size() >= limit || seasonStat(p, stat) <= 0) {
        break;
      }
      final Map<String, String> row = new LinkedHashMap<>();
      final String club = str(p.get("club"));
      row.put("name", str(p.get("name")));
      row.put("club", leagueDataService.getClubName(club));
      row.put("clubSlug", club);
      row.put("value", String.valueOf(seasonStat(p, stat)));
      row.put("href", base + "/players/" + str(p.get("slug")) + ".html");
      row.put("clubHref", base + "/teams/" + club + ".html");
      row.put("base", base);
      rows.add(row);
    }
    return rows;
  }

  private int seasonStat(final Map<String, Object> player, final String stat) {
    final Object season = player.get("season");
    if (!(season instanceof Map)) {
      return 0;
    }
    return asInt(((Map<?, ?>) season).get(stat));
  }
}
