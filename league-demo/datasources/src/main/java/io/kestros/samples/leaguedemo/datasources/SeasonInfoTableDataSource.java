package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
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

    String champion = "—";
    if (season.getChampionId() != null && !season.getChampionId().isEmpty()) {
      Team t = leagueDataService.getTeam(season.getChampionId());
      champion = t != null ? t.getName() : season.getChampionId();
    }
    String runnerUp = "—";
    if (season.getRunnerUpId() != null && !season.getRunnerUpId().isEmpty()) {
      Team t = leagueDataService.getTeam(season.getRunnerUpId());
      runnerUp = t != null ? t.getName() : season.getRunnerUpId();
    }
    String topScorer = "—";
    if (season.getTopScorerId() != null && !season.getTopScorerId().isEmpty()) {
      Player p = leagueDataService.getPlayer(season.getTopScorerId());
      String name = p != null ? p.getFirstName() + " " + p.getLastName() : season.getTopScorerId();
      topScorer = season.getTopScorerGoals() > 0
          ? name + " (" + season.getTopScorerGoals() + " goals)"
          : name;
    }

    try {
      String[][] info = {
          {"Status", capitalize(season.getStatus())},
          {"Champion", champion},
          {"Runner-Up", runnerUp},
          {"Top Scorer", topScorer},
          {"Teams", String.valueOf(season.getTeamIds() != null ? season.getTeamIds().size() : 0)},
          {"Matches", String.valueOf(season.getMatchIds() != null ? season.getMatchIds().size() : 0)},
      };
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
