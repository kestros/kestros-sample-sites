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
 * A player's last-5-matches table. Thin adapter over {@link PlayerService#getLast5Rows}; the player
 * comes from the {@code player} route param on the dynamic route, else the node's {@code player}
 * property.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerLast5TableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @Optional
  private PlayerService playerService;

  private String getPlayerSlug() {
    if (getRequest() != null && getRequest().getAttribute("player") != null) {
      final String player = String.valueOf(getRequest().getAttribute("player"));
      if (!player.isEmpty()) {
        return player;
      }
    }
    return getResource().getValueMap().get("player", "harborside-mateo-reyes");
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String[] labels = {"MW", "Opponent", "Venue", "Result", "Goals", "Assists", "Rating"};
    for (int i = 0; i < labels.length; i++) {
      try {
        final SyntheticTableHeader h = new SyntheticTableHeader(labels[i], this, "tableHeader", "h-" + i);
        if (i >= 4) {
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
    int i = 0;
    for (final Map<String, String> m
        : playerService.getLast5Rows(getPlayerSlug(), getResource().getPath())) {
      try {
        final List<KestrosTableCell> cells = Arrays.asList(
            cell(m.get("mw"), i, 0),
            new SyntheticRichCell(m.get("opp"), "link", m.get("href"), "", m.get("opp"),
                m.get("base"), this, "tableCell", "c-" + i + "-1"),
            cell(m.get("venue"), i, 2),
            new SyntheticRichCell(m.get("res"), "link", m.get("href"), "", m.get("res"),
                m.get("base"), this, "tableCell", "c-" + i + "-3"),
            cell(m.get("g"), i, 4).asNumeric(),
            cell(m.get("a"), i, 5).asNumeric(),
            cell(m.get("rating"), i, 6).asNumeric());
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

  private SyntheticTableCell cell(final String text, final int row, final int col)
      throws ComponentConfigurationException {
    return new SyntheticTableCell(text == null ? "" : text, this, "tableCell",
        "c-" + row + "-" + col);
  }
}
