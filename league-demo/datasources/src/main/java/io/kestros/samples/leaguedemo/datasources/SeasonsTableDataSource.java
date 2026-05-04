package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
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
public class SeasonsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("Season", this, "header", "season"));
      headers.add(new SyntheticTableHeader("Status", this, "header", "status"));
      headers.add(new SyntheticTableHeader("Champion", this, "header", "champ"));
      headers.add(new SyntheticTableHeader("Runner-up", this, "header", "runnerup"));
      headers.add(new SyntheticTableHeader("Golden Boot", this, "header", "boot"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    int i = 0;
    for (Season s : leagueDataService.getSeasons()) {
      String name = s.getName() != null ? s.getName() : s.getStartYear() + "-" + s.getEndYear();
      String status = s.getStatus() != null ? s.getStatus() : "";

      String champ = "-";
      if (s.getChampionId() != null && !s.getChampionId().isEmpty()) {
        Team t = leagueDataService.getTeam(s.getChampionId());
        champ = t != null ? t.getName() : s.getChampionId();
      }

      String runnerUp = "-";
      if (s.getRunnerUpId() != null && !s.getRunnerUpId().isEmpty()) {
        Team t = leagueDataService.getTeam(s.getRunnerUpId());
        runnerUp = t != null ? t.getName() : s.getRunnerUpId();
      }

      String boot = "-";
      if (s.getTopScorerGoals() > 0) {
        boot = s.getTopScorerGoals() + " goals";
      }

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(name, this, "cell", "name-" + i),
            new SyntheticTableCell(status, this, "cell", "status-" + i),
            new SyntheticTableCell(champ, this, "cell", "champ-" + i),
            new SyntheticTableCell(runnerUp, this, "cell", "runnerup-" + i),
            new SyntheticTableCell(boot, this, "cell", "boot-" + i)
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
