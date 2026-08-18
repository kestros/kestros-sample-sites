package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.api.services.StandingsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * League standings table. Thin adapter over {@link StandingsService}; the node configures
 * {@code columns} / {@code headers} / {@code limit} / {@code zones} / {@code clubDisplay}, the
 * service supplies the data (rows, zone classification, rich club-cell fields).
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class StandingsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  private static final String[] ALL_COLUMNS =
      {"pos", "club", "p", "w", "d", "l", "gf", "ga", "gd", "pts"};
  private static final String[] ALL_HEADERS =
      {"#", "Club", "P", "W", "D", "L", "GF", "GA", "GD", "Pts"};

  @OSGiService
  @Optional
  private StandingsService standingsService;

  private String[] getColumnKeys() {
    final String cols = getResource().getValueMap().get("columns", String.class);
    return cols != null && !cols.isEmpty() ? cols.split("\\s*,\\s*") : ALL_COLUMNS;
  }

  private String[] getHeaderLabels() {
    final String hdrs = getResource().getValueMap().get("headers", String.class);
    if (hdrs != null && !hdrs.isEmpty()) {
      return hdrs.split("\\s*,\\s*");
    }
    final String[] cols = getColumnKeys();
    final String[] out = new String[cols.length];
    for (int i = 0; i < cols.length; i++) {
      out[i] = defaultHeaderFor(cols[i]);
    }
    return out;
  }

  private static String defaultHeaderFor(final String col) {
    for (int i = 0; i < ALL_COLUMNS.length; i++) {
      if (ALL_COLUMNS[i].equals(col)) {
        return ALL_HEADERS[i];
      }
    }
    return col;
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String[] labels = getHeaderLabels();
    for (int i = 0; i < labels.length; i++) {
      try {
        final SyntheticTableHeader h = new SyntheticTableHeader(labels[i], this, "tableHeader", "h-" + i);
        if (i > 1) {
          h.asNumeric();
        }
        headers.add(h);
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
    if (standingsService == null) {
      return rows;
    }
    final String[] columns = getColumnKeys();
    final boolean zones = "true".equals(getResource().getValueMap().get("zones", ""));
    final int limit = getResource().getValueMap().get("limit", 0);
    final String clubDisplay = getResource().getValueMap().get("clubDisplay", "");
    int i = 0;
    for (final Map<String, Object> row : selectRows(limit)) {
      try {
        final List<KestrosTableCell> cells = new ArrayList<>();
        for (int c = 0; c < columns.length; c++) {
          if ("club".equals(columns[c])) {
            cells.add(clubCell(String.valueOf(row.get("club")), clubDisplay, i, c));
          } else {
            final Object v = row.get(columns[c]);
            final SyntheticTableCell dataCell = cell(v == null ? "" : String.valueOf(v), i, c);
            if (!"pos".equals(columns[c])) {
              // rank is a label column (centered), not a right-aligned stat
              dataCell.asNumeric();
            }
            cells.add(dataCell);
          }
        }
        rows.add(new SyntheticTableRow(cells, this,
            rowPrefix(zones, String.valueOf(row.get("pos"))), "r-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe: skip a row that fails to build
      }
    }
    return rows;
  }

  /**
   * Rows to render: if the node names {@code clubsAttr} (comma-separated request-attribute names,
   * each resolving to a club slug — e.g. "homeTeam,awayTeam" on match routes), render just those
   * clubs' rows as a mini-table slice; else the usual position-ordered list.
   */
  private List<Map<String, Object>> selectRows(final int limit) {
    final String clubsAttr = getResource().getValueMap().get("clubsAttr", "");
    if (!clubsAttr.isEmpty() && getRequest() != null) {
      final List<String> clubs = new ArrayList<>();
      for (final String attr : clubsAttr.split("\\s*,\\s*")) {
        final Object slug = getRequest().getAttribute(attr);
        if (slug != null && !String.valueOf(slug).isEmpty()) {
          clubs.add(String.valueOf(slug));
        }
      }
      if (!clubs.isEmpty()) {
        return standingsService.getStandingsRowsForClubs(clubs);
      }
    }
    return standingsService.getStandingsRows(limit);
  }

  /** Zone → row prefix, so the table node can style qualify/relegation rows via variations. */
  private String rowPrefix(final boolean zones, final String pos) {
    if (!zones || standingsService == null) {
      return "tableRow";
    }
    int p;
    try {
      p = Integer.parseInt(pos);
    } catch (final NumberFormatException e) {
      return "tableRow";
    }
    switch (standingsService.getZone(p)) {
      case "qualify":
        return "qualifyRow";
      case "relegation":
        return "relegationRow";
      default:
        return "tableRow";
    }
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }

  private SyntheticTableCell cell(final String text, final int row, final int col)
      throws ComponentConfigurationException {
    return new SyntheticTableCell(text, this, "tableCell", "c-" + row + "-" + col);
  }

  private KestrosTableCell clubCell(final String slug, final String clubDisplay, final int row,
      final int col) throws ComponentConfigurationException {
    final Map<String, String> cell =
        standingsService.getClubCell(slug, clubDisplay, getResource().getPath());
    if (!clubDisplay.startsWith("rich")) {
      return cell(cell.get("label"), row, col);
    }
    return new SyntheticClubCell(cell.get("slug"), cell.get("label"), cell.get("linkClass"),
        cell.get("base"), this, "clubCell", "c-" + row + "-" + col);
  }
}
