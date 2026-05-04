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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TopFiveStandingsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  private static final int MAX_ROWS = 5;

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private static class Standing implements Comparable<Standing> {
    String teamId;
    int played, won, drawn, lost, goalsFor, goalsAgainst;
    int points() { return won * 3 + drawn; }
    int goalDifference() { return goalsFor - goalsAgainst; }

    @Override
    public int compareTo(Standing o) {
      if (o.points() != points()) return o.points() - points();
      return o.goalDifference() - goalDifference();
    }
  }

  private List<Standing> computeStandings() {
    Map<String, Standing> map = new HashMap<>();
    if (leagueDataService == null) return new ArrayList<>();

    for (Team t : leagueDataService.getTeams()) {
      Standing s = new Standing();
      s.teamId = t.getId();
      map.put(t.getId(), s);
    }

    for (Match m : leagueDataService.getMatches()) {
      if (!m.isPlayed()) continue;
      Standing home = map.get(m.getHomeTeamId());
      Standing away = map.get(m.getAwayTeamId());
      if (home == null || away == null) continue;

      home.played++; away.played++;
      home.goalsFor += m.getHomeScore(); home.goalsAgainst += m.getAwayScore();
      away.goalsFor += m.getAwayScore(); away.goalsAgainst += m.getHomeScore();

      if (m.getHomeScore() > m.getAwayScore()) { home.won++; away.lost++; }
      else if (m.getHomeScore() < m.getAwayScore()) { away.won++; home.lost++; }
      else { home.drawn++; away.drawn++; }
    }

    List<Standing> standings = new ArrayList<>(map.values());
    standings.sort(null);
    return standings;
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("#", this, "header", "pos"));
      headers.add(new SyntheticTableHeader("Club", this, "header", "club"));
      headers.add(new SyntheticTableHeader("P", this, "header", "played"));
      headers.add(new SyntheticTableHeader("GD", this, "header", "gd"));
      headers.add(new SyntheticTableHeader("Pts", this, "header", "pts"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    List<Standing> standings = computeStandings();

    int pos = 0;
    for (Standing s : standings) {
      pos++;
      if (pos > MAX_ROWS) break;
      Team team = leagueDataService.getTeam(s.teamId);
      String name = team != null ? team.getName() : s.teamId;
      int gd = s.goalDifference();
      String gdStr = (gd > 0 ? "+" : "") + gd;

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(String.valueOf(pos), this, "cell", "pos-" + pos),
            new SyntheticTableCell(name, this, "cell", "club-" + pos),
            new SyntheticTableCell(String.valueOf(s.played), this, "cell", "p-" + pos),
            new SyntheticTableCell(gdStr, this, "cell", "gd-" + pos),
            new SyntheticTableCell(String.valueOf(s.points()), this, "cell", "pts-" + pos)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + pos));
      } catch (Exception e) { /* skip */ }
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
