package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Team;
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
public class TopGoalsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  private static final int DEFAULT_LIMIT = 5;

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  int getLimit() {
    return getResource().getValueMap().get("maxRows", DEFAULT_LIMIT);
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("#", this, "header", "rank"));
      headers.add(new SyntheticTableHeader("Player", this, "header", "player"));
      headers.add(new SyntheticTableHeader("Club", this, "header", "club"));
      headers.add(new SyntheticTableHeader("Apps", this, "header", "apps"));
      headers.add(new SyntheticTableHeader("Goals", this, "header", "goals"));
      headers.add(new SyntheticTableHeader("G/Match", this, "header", "rate"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    List<Player> top = leagueDataService.getPlayers().stream()
        .filter(p -> p.getGoals() > 0)
        .sorted(Comparator.comparingInt(Player::getGoals).reversed())
        .limit(getLimit())
        .collect(Collectors.toList());

    int rank = 0;
    for (Player p : top) {
      rank++;
      Team team = leagueDataService.getTeam(p.getTeamId());
      String teamName = team != null ? team.getName() : p.getTeamId();
      String name = p.getFirstName() + " " + p.getLastName();

      String rate = p.getAppearances() > 0
          ? String.format("%.2f", (double) p.getGoals() / p.getAppearances())
          : "—";
      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(String.valueOf(rank), this, "cell", "rank-" + rank),
            new SyntheticTableCell(name, this, "cell", "name-" + rank),
            new SyntheticTableCell(teamName, this, "cell", "club-" + rank),
            new SyntheticTableCell(String.valueOf(p.getAppearances()), this, "cell", "apps-" + rank),
            new SyntheticTableCell(String.valueOf(p.getGoals()), this, "cell", "goals-" + rank),
            new SyntheticTableCell(rate, this, "cell", "rate-" + rank)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + rank));
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
