package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.api.services.PlayerService;
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
 * League-wide leaders table (players index). Thin adapter over
 * {@link PlayerService#getLeagueLeaders}; the node configures {@code stat} ("goals" or "assists")
 * and {@code limit} (default 10). Player and club cells link to their pages.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class LeagueLeadersTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @Optional
  private PlayerService playerService;

  private String getStat() {
    return getResource().getValueMap().get("stat", "goals");
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String statLabel = "assists".equals(getStat()) ? "Assists" : "Goals";
    final String[] labels = {"#", "Player", "Club", statLabel};
    for (int i = 0; i < labels.length; i++) {
      try {
        final SyntheticTableHeader h = new SyntheticTableHeader(labels[i], this, "tableHeader", "h-" + i);
        if (i == 0 || i == 3) {
          h.asNumeric();
        }
        headers.add(h);
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
    if (playerService == null) {
      return rows;
    }
    final int limit = getResource().getValueMap().get("limit", 10);
    int i = 0;
    for (final Map<String, String> p
        : playerService.getLeagueLeaders(getStat(), limit, getResource().getPath())) {
      try {
        final List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(String.valueOf(i + 1), this, "tableCell", "c-" + i + "-0")
                .asNumeric(),
            new SyntheticRichCell(p.get("name"), "link", p.get("href"), "", p.get("name"),
                p.get("base"), this, "tableCell", "c-" + i + "-1"),
            new SyntheticClubCell(p.get("clubSlug"), p.get("club"), "club-link", p.get("base"),
                this, "clubCell", "c-" + i + "-2"),
            new SyntheticTableCell(p.get("value"), this, "tableCell", "c-" + i + "-3")
                .asNumeric());
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
}
