package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Per-match statline derived from the match id and the final scoreline. Numbers are
 * deterministic (a given match always renders the same stats) and internally consistent —
 * the winning side is biased toward higher possession and shot counts, shots-on-target
 * never exceed shots, and possession adds to 100%.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchStatsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private Match getMatch() {
    if (leagueDataService == null) return null;
    String id = (String) getRequest().getAttribute("match-id");
    return id != null ? leagueDataService.getMatch(id) : null;
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    Match match = getMatch();
    if (match == null || leagueDataService == null || !match.isPlayed()) return headers;

    Team home = leagueDataService.getTeam(match.getHomeTeamId());
    Team away = leagueDataService.getTeam(match.getAwayTeamId());
    try {
      headers.add(new SyntheticTableHeader(home != null ? home.getName() : "", this, "header", "home"));
      headers.add(new SyntheticTableHeader("", this, "header", "stat"));
      headers.add(new SyntheticTableHeader(away != null ? away.getName() : "", this, "header", "away"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    Match match = getMatch();
    if (match == null || !match.isPlayed()) return rows;

    int diff = match.getHomeScore() - match.getAwayScore();
    int homePossession = bounded(match.getId(), "possession", 38, 62) + (diff * 2);
    homePossession = Math.max(35, Math.min(65, homePossession));
    int awayPossession = 100 - homePossession;

    int homeShots = bounded(match.getId(), "shotsHome", 7, 19)
        + Math.max(0, diff * 2);
    int awayShots = bounded(match.getId(), "shotsAway", 7, 19)
        + Math.max(0, -diff * 2);
    int homeSot = Math.min(homeShots,
        match.getHomeScore() + bounded(match.getId(), "sotHome", 1, 5));
    int awaySot = Math.min(awayShots,
        match.getAwayScore() + bounded(match.getId(), "sotAway", 1, 5));
    int homeCorners = bounded(match.getId(), "cornersHome", 2, 9) + Math.max(0, diff);
    int awayCorners = bounded(match.getId(), "cornersAway", 2, 9) + Math.max(0, -diff);
    int homeFouls = bounded(match.getId(), "foulsHome", 6, 16);
    int awayFouls = bounded(match.getId(), "foulsAway", 6, 16);
    int homeYellow = bounded(match.getId(), "yellowHome", 0, 4);
    int awayYellow = bounded(match.getId(), "yellowAway", 0, 4);

    String[][] stats = {
        {homePossession + "%", "Possession", awayPossession + "%"},
        {String.valueOf(homeShots), "Shots", String.valueOf(awayShots)},
        {String.valueOf(homeSot), "Shots on target", String.valueOf(awaySot)},
        {String.valueOf(homeCorners), "Corners", String.valueOf(awayCorners)},
        {String.valueOf(homeFouls), "Fouls", String.valueOf(awayFouls)},
        {String.valueOf(homeYellow), "Yellow cards", String.valueOf(awayYellow)},
    };

    int i = 0;
    for (String[] stat : stats) {
      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(stat[0], this, "cell", "home-" + i),
            new SyntheticTableCell(stat[1], this, "cell", "label-" + i),
            new SyntheticTableCell(stat[2], this, "cell", "away-" + i)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + i));
        i++;
      } catch (Exception e) { /* skip */ }
    }
    return rows;
  }

  /**
   * Deterministic value in [min, max] derived from a stable hash of the match id and the
   * stat name. Same input always yields the same output, which keeps the displayed line
   * stable across refreshes.
   */
  private int bounded(String matchId, String stat, int min, int max) {
    int h = (matchId + ':' + stat).hashCode();
    int span = max - min + 1;
    return min + Math.floorMod(h, span);
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
