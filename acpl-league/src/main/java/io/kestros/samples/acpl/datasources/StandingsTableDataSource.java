package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.services.LeagueDataService;
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
 * League standings table, fed from {@link LeagueDataService}. Optionally trims to the top-N via a
 * {@code limit} property so the same datasource serves the home widget (top 5) and the full table.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class StandingsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private static final String[] ALL_COLUMNS =
      {"pos", "club", "p", "w", "d", "l", "gf", "ga", "gd", "pts"};
  private static final String[] ALL_HEADERS =
      {"#", "Club", "P", "W", "D", "L", "GF", "GA", "GD", "Pts"};

  private int getLimit() {
    return getResource().getValueMap().get("limit", 0);
  }

  /** Field keys to render, in order. Defaults to the full 10-column table. */
  private String[] getColumnKeys() {
    final String cols = getResource().getValueMap().get("columns", String.class);
    return cols != null && !cols.isEmpty() ? cols.split("\\s*,\\s*") : ALL_COLUMNS;
  }

  /** Header labels, in order. Defaults to the full 10-column labels. */
  private String[] getHeaderLabels() {
    final String hdrs = getResource().getValueMap().get("headers", String.class);
    if (hdrs != null && !hdrs.isEmpty()) {
      return hdrs.split("\\s*,\\s*");
    }
    // derive default headers to match the configured columns
    final String[] cols = getColumnKeys();
    if (cols == ALL_COLUMNS) {
      return ALL_HEADERS;
    }
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
    final String[] labels = getHeaderLabels();
    for (int i = 0; i < labels.length; i++) {
      try {
        headers.add(new SyntheticTableHeader(labels[i], this, "tableHeader", "h-" + i));
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
    final String[] columns = getColumnKeys();
    int i = 0;
    for (final Map<String, Object> row : rows()) {
      try {
        final List<KestrosTableCell> cells = new ArrayList<>();
        for (int c = 0; c < columns.length; c++) {
          if ("club".equals(columns[c])) {
            cells.add(isRichClub()
                ? clubCell(str(row.get("club")), i, c)
                : cell(leagueDataService.getClubName(str(row.get("club"))), i, c));
          } else {
            cells.add(cell(str(row.get(columns[c])), i, c));
          }
        }
        rows.add(new SyntheticTableRow(cells, this, "tableRow", "r-" + i));
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
      throws ComponentConfigurationException {
    return new SyntheticTableCell(text == null ? "" : text, this, "tableCell", "c-" + row + "-" + col);
  }

  /**
   * Rich club cell modes: {@code rich} (compact widget — crest + abbreviation) or {@code rich-name}
   * (full table — crest + full club name, bolded).
   */
  private boolean isRichClub() {
    return getClubDisplay().startsWith("rich");
  }

  private String getClubDisplay() {
    return getResource().getValueMap().get("clubDisplay", "");
  }

  /**
   * Rich club cell matching the static mock: a linked crest image plus a club link, both pointing at
   * the club's team page. Rendered by the {@code club} table-cell layout; the {@code clubCell} prefix
   * lets the table node style it independently from the plain cells.
   */
  private KestrosTableCell clubCell(final String slug, final int row, final int col)
      throws ComponentConfigurationException {
    final boolean useName = "rich-name".equals(getClubDisplay());
    final String label = useName
        ? leagueDataService.getClubName(slug)
        : leagueDataService.getClubShort(slug);
    final String linkClass = useName ? "fw-semibold club-link" : "club-link";
    return new SyntheticClubCell(slug, label, linkClass, siteRoot(),
        this, "clubCell", "c-" + row + "-" + col);
  }

  /**
   * League-site root (e.g. {@code /content/sites/acpl}), derived from this datasource's path — the
   * first three path segments under {@code /content/sites}, so crest/team links are site-absolute
   * regardless of which page (home vs. an inner page like {@code /standings}) hosts the table.
   */
  private String siteRoot() {
    final String path = getResource().getPath();
    final java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("^(/content/sites/[^/]+)").matcher(path);
    return m.find() ? m.group(1) : path;
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
