package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
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
public class TopFiveStandingsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  private static final int DEFAULT_MAX_ROWS = 5;

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  int getMaxRows() {
    return getResource().getValueMap().get("maxRows", DEFAULT_MAX_ROWS);
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
    int max = getMaxRows();

    int pos = 0;
    for (StandingsCalculator.Standing s : StandingsCalculator.compute(leagueDataService)) {
      pos++;
      if (pos > max) break;
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
