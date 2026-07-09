package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * League standings table, fed from {@link LeagueDataService}. Optionally trims to the top-N via a
 * {@code limit} property so the same datasource serves the home widget (top 5) and the full table.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class StandingsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private int getLimit() {
    return getResource().getValueMap().get("limit", 0);
  }

  private List<Map<String, Object>> rows() {
    if (leagueDataService == null) {
      return new ArrayList<>();
    }
    final List<Map<String, Object>> all = leagueDataService.getStandings();
    final int limit = getLimit();
    if (limit > 0 && limit < all.size()) {
      return all.subList(0, limit);
    }
    return all;
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String[] labels = {"#", "Club", "P", "W", "D", "L", "GF", "GA", "GD", "Pts"};
    for (int i = 0; i < labels.length; i++) {
      try {
        headers.add(new SyntheticTableHeader(labels[i], this, "header", "h-" + i));
      } catch (final Exception e) {
        // null-safe: skip a header that fails to build
      }
    }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    final List<KestrosTableRow> rows = new ArrayList<>();
    int i = 0;
    for (final Map<String, Object> row : rows()) {
      try {
        final String club = str(row.get("club"));
        final List<KestrosTableCell> cells = Arrays.asList(
            cell(str(row.get("pos")), i, 0),
            cell(leagueDataService.getClubName(club), i, 1),
            cell(str(row.get("p")), i, 2),
            cell(str(row.get("w")), i, 3),
            cell(str(row.get("d")), i, 4),
            cell(str(row.get("l")), i, 5),
            cell(str(row.get("gf")), i, 6),
            cell(str(row.get("ga")), i, 7),
            cell(str(row.get("gd")), i, 8),
            cell(str(row.get("pts")), i, 9));
        rows.add(new SyntheticTableRow(cells, this, "row", "r-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe: skip a row that fails to build
      }
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }

  private KestrosTableCell cell(final String text, final int row, final int col)
      throws io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException {
    return new SyntheticTableCell(text == null ? "" : text, this, "cell", "c-" + row + "-" + col);
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
