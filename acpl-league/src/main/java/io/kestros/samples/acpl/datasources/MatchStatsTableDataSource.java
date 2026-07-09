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
 * Match statistics table (Home / Stat / Away), fed from the featured match's {@code stats} in
 * {@link LeagueDataService#getFeaturedMatch()}.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchStatsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> stats() {
    if (leagueDataService == null) {
      return new ArrayList<>();
    }
    final Object s = leagueDataService.getFeaturedMatch().get("stats");
    return s instanceof List ? (List<Map<String, Object>>) s : new ArrayList<>();
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String[] labels = {"Home", "Stat", "Away"};
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
    int i = 0;
    for (final Map<String, Object> s : stats()) {
      try {
        final List<KestrosTableCell> cells = Arrays.asList(
            cell(str(s.get("h")), i, 0),
            cell(str(s.get("label")), i, 1),
            cell(str(s.get("a")), i, 2));
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

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
