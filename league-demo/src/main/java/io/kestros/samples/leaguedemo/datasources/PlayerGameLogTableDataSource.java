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
import java.util.List;
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
    if (slug == null) {
      return null;
    }
    return leagueDataService.getPlayer(slug);
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("Wk", this, "header", "wk"));
      headers.add(new SyntheticTableHeader("Opponent", this, "header", "opp"));
      headers.add(new SyntheticTableHeader("Venue", this, "header", "venue"));
      headers.add(new SyntheticTableHeader("Min", this, "header", "min"));
      headers.add(new SyntheticTableHeader("G", this, "header", "goals"));
      headers.add(new SyntheticTableHeader("A", this, "header", "assists"));
    } catch (Exception e) {
      // null-safe
    }
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

    int rowIndex = 0;
    for (Match match : leagueDataService.getMatches()) {
      if (!match.isPlayed()) {
        continue;
      }
      String teamId = player.getTeamId();
      if (!teamId.equals(match.getHomeTeamId()) && !teamId.equals(match.getAwayTeamId())) {
        continue;
      }
      boolean isHome = teamId.equals(match.getHomeTeamId());
      String oppId = isHome ? match.getAwayTeamId() : match.getHomeTeamId();
      Team opp = leagueDataService.getTeam(oppId);
      String oppName = opp != null ? opp.getName() : oppId;
      String venue = isHome ? "H" : "A";

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell("W" + match.getMatchday(), this, "cell", "wk-" + rowIndex),
            new SyntheticTableCell(oppName, this, "cell", "opp-" + rowIndex),
            new SyntheticTableCell(venue, this, "cell", "venue-" + rowIndex),
            new SyntheticTableCell("90'", this, "cell", "min-" + rowIndex),
            new SyntheticTableCell("-", this, "cell", "goals-" + rowIndex),
            new SyntheticTableCell("-", this, "cell", "assists-" + rowIndex)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + rowIndex));
        rowIndex++;
      } catch (Exception e) {
        // skip on error
      }
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
