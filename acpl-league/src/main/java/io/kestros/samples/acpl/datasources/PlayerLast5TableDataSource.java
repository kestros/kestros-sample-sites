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
 * A player's last-5-matches table, fed from {@link LeagueDataService#getPlayer(String)}. The player
 * is taken from a {@code player} slug property on the table.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerLast5TableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private String getPlayerSlug() {
    return getResource().getValueMap().get("player", "harborside-mateo-reyes");
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> last5() {
    if (leagueDataService == null) {
      return new ArrayList<>();
    }
    final Object l5 = leagueDataService.getPlayer(getPlayerSlug()).get("last5");
    return l5 instanceof List ? (List<Map<String, Object>>) l5 : new ArrayList<>();
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String[] labels = {"Opponent", "Venue", "Goals", "Assists", "Rating"};
    for (int i = 0; i < labels.length; i++) {
      try {
        headers.add(new SyntheticTableHeader(labels[i], this, "header", "h-" + i));
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
    for (final Map<String, Object> m : last5()) {
      try {
        final String opp = leagueDataService.getClubName(str(m.get("opp")));
        final List<KestrosTableCell> cells = Arrays.asList(
            cell(opp, i, 0),
            cell(str(m.get("venue")), i, 1),
            cell(str(m.get("g")), i, 2),
            cell(str(m.get("a")), i, 3),
            cell(str(m.get("rating")), i, 4));
        rows.add(new SyntheticTableRow(cells, this, "row", "r-" + i));
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
    return new SyntheticTableCell(text == null ? "" : text, this, "cell", "c-" + row + "-" + col);
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
