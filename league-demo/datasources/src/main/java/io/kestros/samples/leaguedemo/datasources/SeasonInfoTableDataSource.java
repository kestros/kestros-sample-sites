package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonInfoTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("", this, "header", "label"));
      headers.add(new SyntheticTableHeader("", this, "header", "value"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    String id = (String) getRequest().getAttribute("season-id");
    if (id == null) return rows;

    Season season = leagueDataService.getSeason(id);
    if (season == null) return rows;

    boolean inProgress = "in-progress".equals(season.getStatus());

    List<String[]> info = new ArrayList<>();
    info.add(new String[]{"Status", capitalize(season.getStatus())});

    String championLabel = inProgress ? "Current leader" : "Champion";
    String runnerUpLabel = inProgress ? "Currently 2nd" : "Runner-Up";
    String topScorerLabel = inProgress ? "Golden Boot leader" : "Top Scorer";

    String[] standings = inProgress
        ? topTwoByPoints(id)
        : new String[]{nameOf(season.getChampionId()), nameOf(season.getRunnerUpId())};
    info.add(new String[]{championLabel, standings[0] != null ? standings[0] : "—"});
    info.add(new String[]{runnerUpLabel, standings[1] != null ? standings[1] : "—"});

    String topScorer;
    if (inProgress) {
      topScorer = currentTopScorer();
    } else if (season.getTopScorerId() != null && !season.getTopScorerId().isEmpty()) {
      Player p = leagueDataService.getPlayer(season.getTopScorerId());
      String name = p != null
          ? p.getFirstName() + " " + p.getLastName()
          : season.getTopScorerId();
      topScorer = season.getTopScorerGoals() > 0
          ? name + " (" + season.getTopScorerGoals() + " goals)"
          : name;
    } else {
      topScorer = "—";
    }
    info.add(new String[]{topScorerLabel, topScorer != null ? topScorer : "—"});

    int teamCount = season.getTeamIds() != null ? season.getTeamIds().size() : 0;
    info.add(new String[]{"Teams", String.valueOf(teamCount)});

    long total = leagueDataService.getMatches().stream()
        .filter(m -> id.equals(m.getSeasonId()))
        .count();
    if (inProgress) {
      long played = leagueDataService.getMatches().stream()
          .filter(m -> id.equals(m.getSeasonId()))
          .filter(Match::isPlayed)
          .count();
      info.add(new String[]{"Matches",
          played + " played / " + total + " scheduled"});
    } else if (total > 0) {
      info.add(new String[]{"Matches", String.valueOf(total)});
    }

    try {
      int i = 0;
      for (String[] entry : info) {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(entry[0], this, "cell", "label-" + i),
            new SyntheticTableCell(entry[1], this, "cell", "value-" + i)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + i));
        i++;
      }
    } catch (Exception e) { /* skip */ }
    return rows;
  }

  private String nameOf(String teamId) {
    if (teamId == null || teamId.isEmpty()) return null;
    Team t = leagueDataService.getTeam(teamId);
    return t != null ? t.getName() : teamId;
  }

  /**
   * Returns the in-progress season's current top two teams (by the shared
   * StandingsCalculator ranking). Entries are null if no matches have been played yet.
   */
  private String[] topTwoByPoints(String seasonId) {
    List<StandingsCalculator.Standing> table =
        StandingsCalculator.compute(leagueDataService, seasonId);
    if (table.isEmpty() || table.get(0).played == 0) {
      return new String[]{null, null};
    }
    String first = formatLeader(table.get(0));
    String second = table.size() > 1 ? formatLeader(table.get(1)) : null;
    return new String[]{first, second};
  }

  private String formatLeader(StandingsCalculator.Standing s) {
    Team t = leagueDataService.getTeam(s.teamId);
    String name = t != null ? t.getName() : s.teamId;
    return name + " (" + s.points() + " pts)";
  }

  private String currentTopScorer() {
    return leagueDataService.getPlayers().stream()
        .filter(p -> p.getGoals() > 0)
        .max(Comparator.comparingInt(Player::getGoals))
        .map(p -> p.getFirstName() + " " + p.getLastName()
            + " (" + p.getGoals() + " goals)")
        .orElse("—");
  }

  private static String capitalize(String s) {
    if (s == null || s.isEmpty()) return "";
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
