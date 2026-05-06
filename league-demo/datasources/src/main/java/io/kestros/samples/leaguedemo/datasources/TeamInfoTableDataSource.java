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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamInfoTableDataSource extends BaseContainerSlingModelDataSource
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

    String slug = (String) getRequest().getAttribute("team-slug");
    if (slug == null) return rows;

    Team team = leagueDataService.getTeam(slug);
    if (team == null) return rows;

    List<String[]> info = new ArrayList<>();
    if (StringUtils.isNotBlank(team.getNickname())) {
      info.add(new String[]{"Nickname", team.getNickname()});
    }
    info.add(new String[]{"City", team.getCity()});
    if (StringUtils.isNotBlank(team.getManager())) {
      info.add(new String[]{"Manager", team.getManager()});
    }
    if (team.getFounded() > 0) {
      info.add(new String[]{"Founded", String.valueOf(team.getFounded())});
    }
    String stadium = team.getStadium();
    if (team.getVenueOpened() > 0) {
      stadium = stadium + " (opened " + team.getVenueOpened() + ")";
    }
    info.add(new String[]{"Stadium", stadium});
    if (team.getStadiumCapacity() > 0) {
      info.add(new String[]{"Capacity", String.format("%,d", team.getStadiumCapacity())});
    }
    if (StringUtils.isNotBlank(team.getSurface())) {
      info.add(new String[]{"Surface", team.getSurface()});
    }
    if (StringUtils.isNotBlank(team.getVenueAddress())) {
      info.add(new String[]{"Address", team.getVenueAddress()});
    }

    info.add(new String[]{"Squad size",
        String.valueOf(leagueDataService.getPlayersByTeam(slug).size())});

    String position = leaguePosition(slug);
    if (position != null) {
      info.add(new String[]{"League position", position});
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

  /**
   * Computes the team's current league position by replaying played matches and ranking
   * by points (3-1-0), then goal difference, then goals for. Returns "Nth of M" so the
   * profile makes sense in any league size.
   */
  private String leaguePosition(String slug) {
    Map<String, int[]> stats = new HashMap<>();
    for (Team t : leagueDataService.getTeams()) {
      stats.put(t.getId(), new int[]{0, 0, 0}); // points, gd, gf
    }
    for (Match m : leagueDataService.getMatches()) {
      if (!m.isPlayed()) continue;
      int[] home = stats.get(m.getHomeTeamId());
      int[] away = stats.get(m.getAwayTeamId());
      if (home == null || away == null) continue;
      int hs = m.getHomeScore();
      int as = m.getAwayScore();
      home[1] += hs - as;
      away[1] += as - hs;
      home[2] += hs;
      away[2] += as;
      if (hs > as) home[0] += 3;
      else if (as > hs) away[0] += 3;
      else { home[0] += 1; away[0] += 1; }
    }
    List<Map.Entry<String, int[]>> ranked = new ArrayList<>(stats.entrySet());
    ranked.sort(Comparator
        .comparingInt((Map.Entry<String, int[]> e) -> e.getValue()[0]).reversed()
        .thenComparingInt((Map.Entry<String, int[]> e) -> e.getValue()[1]).reversed()
        .thenComparingInt((Map.Entry<String, int[]> e) -> e.getValue()[2]).reversed());
    for (int i = 0; i < ranked.size(); i++) {
      if (slug.equals(ranked.get(i).getKey())) {
        int rank = i + 1;
        String suffix;
        if (rank % 100 >= 11 && rank % 100 <= 13) suffix = "th";
        else switch (rank % 10) {
          case 1: suffix = "st"; break;
          case 2: suffix = "nd"; break;
          case 3: suffix = "rd"; break;
          default: suffix = "th";
        }
        return rank + suffix + " of " + ranked.size();
      }
    }
    return null;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
