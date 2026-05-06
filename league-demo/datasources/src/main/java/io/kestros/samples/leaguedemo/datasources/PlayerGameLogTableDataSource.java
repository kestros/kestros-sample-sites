package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerGameLogTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private Player getPlayer() {
    if (leagueDataService == null) {
      return null;
    }
    String slug = (String) getRequest().getAttribute("player-slug");
    return slug != null ? leagueDataService.getPlayer(slug) : null;
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("Wk", this, "header", "wk"));
      headers.add(new SyntheticTableHeader("Opponent", this, "header", "opp"));
      headers.add(new SyntheticTableHeader("Venue", this, "header", "venue"));
      headers.add(new SyntheticTableHeader("Result", this, "header", "result"));
      headers.add(new SyntheticTableHeader("G", this, "header", "goals"));
      headers.add(new SyntheticTableHeader("A", this, "header", "assists"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    Player player = getPlayer();
    if (player == null || leagueDataService == null) {
      return rows;
    }

    String teamId = player.getTeamId();
    List<Match> appearances = new ArrayList<>();
    for (Match match : leagueDataService.getMatches()) {
      if (!match.isPlayed()) continue;
      if (!teamId.equals(match.getHomeTeamId()) && !teamId.equals(match.getAwayTeamId())) {
        continue;
      }
      appearances.add(match);
    }

    Map<String, Integer> goalsByMatch = distribute(appearances, player.getId() + ":G",
        player.getGoals());
    Map<String, Integer> assistsByMatch = distribute(appearances, player.getId() + ":A",
        player.getAssists());

    int rowIndex = 0;
    for (Match match : appearances) {
      boolean isHome = teamId.equals(match.getHomeTeamId());
      String oppId = isHome ? match.getAwayTeamId() : match.getHomeTeamId();
      Team opp = leagueDataService.getTeam(oppId);
      String oppName = opp != null ? opp.getName() : oppId;
      String venue = isHome ? "H" : "A";
      int teamScore = isHome ? match.getHomeScore() : match.getAwayScore();
      int oppScore = isHome ? match.getAwayScore() : match.getHomeScore();
      String resultPrefix;
      if (teamScore > oppScore) resultPrefix = "W";
      else if (teamScore < oppScore) resultPrefix = "L";
      else resultPrefix = "D";
      String result = resultPrefix + " " + teamScore + "-" + oppScore;
      int g = goalsByMatch.getOrDefault(match.getId(), 0);
      int a = assistsByMatch.getOrDefault(match.getId(), 0);

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell("W" + match.getMatchday(), this, "cell", "wk-" + rowIndex),
            new SyntheticTableCell(oppName, this, "cell", "opp-" + rowIndex),
            new SyntheticTableCell(venue, this, "cell", "venue-" + rowIndex),
            new SyntheticTableCell(result, this, "cell", "result-" + rowIndex),
            new SyntheticTableCell(g > 0 ? String.valueOf(g) : "-",
                this, "cell", "goals-" + rowIndex),
            new SyntheticTableCell(a > 0 ? String.valueOf(a) : "-",
                this, "cell", "assists-" + rowIndex)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + rowIndex));
        rowIndex++;
      } catch (Exception e) { /* skip */ }
    }
    return rows;
  }

  /**
   * Distributes a season total of contributions across the player's played matches in a
   * deterministic way: each match is scored by a stable hash of (player + match id), the
   * matches are ranked by that score, and the top {@code total} matches each receive +1.
   * If a match's hash bucket also lands in the very top tier, it gets +2 (multi-goal
   * games for prolific scorers). The sum across rows always equals {@code total}.
   */
  private Map<String, Integer> distribute(List<Match> matches, String seed, int total) {
    Map<String, Integer> out = new HashMap<>();
    if (total <= 0 || matches.isEmpty()) return out;
    List<Match> ranked = new ArrayList<>(matches);
    ranked.sort(Comparator.comparingInt(m -> -((seed + m.getId()).hashCode())));
    int remaining = total;
    int idx = 0;
    while (remaining > 0 && idx < ranked.size()) {
      out.merge(ranked.get(idx).getId(), 1, Integer::sum);
      remaining--;
      idx++;
      if (idx == ranked.size() && remaining > 0) {
        idx = 0;
      }
    }
    return out;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
