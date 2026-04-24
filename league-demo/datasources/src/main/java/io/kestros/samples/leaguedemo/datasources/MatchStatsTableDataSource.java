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
    if (match == null || leagueDataService == null) return headers;

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
    if (match == null) return rows;

    // Placeholder stats — in a real implementation these would come from match data
    String[][] stats = {
        {"58%", "Possession", "42%"},
        {"14", "Shots", "9"},
        {"7", "Shots on target", "3"},
        {"6", "Corners", "4"},
        {"11", "Fouls", "14"},
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

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
