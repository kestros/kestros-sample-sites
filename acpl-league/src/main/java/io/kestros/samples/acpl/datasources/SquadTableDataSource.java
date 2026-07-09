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
 * Club squad table, fed from {@link LeagueDataService#getSquad(String)}. The club is taken from a
 * {@code club} property on the table (e.g. {@code club="harborside"}).
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SquadTableDataSource extends BaseContainerSlingModelDataSource implements KestrosTable {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private String getClub() {
    return getResource().getValueMap().get("club", "harborside");
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String[] labels = {"#", "Name", "Pos", "Age", "Apps", "G", "A"};
    for (int i = 0; i < labels.length; i++) {
      try {
        headers.add(new SyntheticTableHeader(labels[i], this, "tableHeader", "h-" + i));
      } catch (final Exception e) {
        // null-safe
      }
    }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    final List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) {
      return rows;
    }
    int i = 0;
    for (final Map<String, Object> p : leagueDataService.getSquad(getClub())) {
      try {
        final Map<String, Object> season = asMap(p.get("season"));
        final List<KestrosTableCell> cells = Arrays.asList(
            cell(str(p.get("num")), i, 0),
            cell(str(p.get("name")), i, 1),
            cell(str(p.get("pos")), i, 2),
            cell(str(p.get("age")), i, 3),
            cell(str(season.get("apps")), i, 4),
            cell(str(season.get("goals")), i, 5),
            cell(str(season.get("assists")), i, 6));
        rows.add(new SyntheticTableRow(cells, this, "tableRow", "r-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe
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
    return new SyntheticTableCell(text == null ? "" : text, this, "tableCell", "c-" + row + "-" + col);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> asMap(final Object o) {
    return o instanceof Map ? (Map<String, Object>) o : new java.util.HashMap<>();
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
