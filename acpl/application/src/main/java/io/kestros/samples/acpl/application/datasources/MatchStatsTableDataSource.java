package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.api.services.MatchService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Match statistics table (Home / Stat / Away). Thin adapter over
 * {@link MatchService#getMatchStatRows}; the match comes from the {@code match} route param
 * (featured match when absent).
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchStatsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  private static final Logger LOG = LoggerFactory.getLogger(MatchStatsTableDataSource.class);

  @OSGiService
  @Optional
  private MatchService matchService;

  private String getMatchId() {
    if (getRequest() != null && getRequest().getAttribute("match") != null) {
      final String match = String.valueOf(getRequest().getAttribute("match"));
      if (!match.isEmpty()) {
        return match;
      }
    }
    return getResource().getValueMap().get("match", "");
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
        LOG.error("header cell {} failed; the table will misalign: {}", i, e.getMessage(), e);
        // null-safe
      }
    }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    final List<KestrosTableRow> rows = new ArrayList<>();
    if (matchService == null) {
      return rows;
    }
    int i = 0;
    for (final Map<String, String> s : matchService.getMatchStatRows(getMatchId())) {
      try {
        final List<KestrosTableCell> cells = Arrays.asList(
            cell(s.get("h"), i, 0),
            cell(s.get("label"), i, 1),
            cell(s.get("a"), i, 2));
        rows.add(new SyntheticTableRow(cells, this, "tableRow", "r-" + i));
        i++;
      } catch (final Exception e) {
        LOG.error("row skipped: {}", e.getMessage(), e);
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
      throws ComponentConfigurationException {
    return new SyntheticTableCell(text == null ? "" : text, this, "tableCell",
        "c-" + row + "-" + col);
  }
}
