package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamRosterTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("#", this, "header", "num"));
      headers.add(new SyntheticTableHeader("Name", this, "header", "name"));
      headers.add(new SyntheticTableHeader("Position", this, "header", "pos"));
      headers.add(new SyntheticTableHeader("Apps", this, "header", "apps"));
      headers.add(new SyntheticTableHeader("G", this, "header", "goals"));
      headers.add(new SyntheticTableHeader("A", this, "header", "assists"));
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

    List<Player> roster = leagueDataService.getPlayersByTeam(slug).stream()
        .sorted(Comparator
            .comparingInt((Player p) -> positionOrder(p.getPosition()))
            .thenComparingInt(Player::getNumber))
        .collect(Collectors.toList());

    int i = 0;
    for (Player p : roster) {
      try {
        int apps = PlayerAppearances.displayedFor(p, leagueDataService);
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(String.valueOf(p.getNumber()), this, "cell", "num-" + i),
            new SyntheticTableCell(p.getFirstName() + " " + p.getLastName(),
                this, "cell", "name-" + i),
            new SyntheticTableCell(p.getPosition() != null ? p.getPosition() : "—",
                this, "cell", "pos-" + i),
            new SyntheticTableCell(String.valueOf(apps), this, "cell", "apps-" + i),
            new SyntheticTableCell(String.valueOf(p.getGoals()), this, "cell", "goals-" + i),
            new SyntheticTableCell(String.valueOf(p.getAssists()), this, "cell", "assists-" + i)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + i));
        i++;
      } catch (Exception e) { /* skip */ }
    }
    return rows;
  }

  /**
   * Orders positions in standard squad-list sequence: Goalkeepers first, then Defenders,
   * Midfielders, Forwards. Anything unrecognised drops to the end.
   */
  private static int positionOrder(String position) {
    if (position == null) return 99;
    switch (position) {
      case "Goalkeeper": return 0;
      case "Defender": return 1;
      case "Midfielder": return 2;
      case "Forward": return 3;
      default: return 99;
    }
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
