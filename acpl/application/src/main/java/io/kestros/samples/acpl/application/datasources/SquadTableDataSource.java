package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.api.services.TeamService;
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
 * Club squad table (#, photo, Name, Pos, Age, Apps, G, A). Thin adapter over
 * {@link TeamService#getSquadRows}; the club comes from the {@code team} route param on the dynamic
 * route, else the node's {@code club} property.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SquadTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @Optional
  private TeamService teamService;

  private String getClub() {
    if (getRequest() != null && getRequest().getAttribute("team") != null) {
      final String team = String.valueOf(getRequest().getAttribute("team"));
      if (!team.isEmpty()) {
        return team;
      }
    }
    return getResource().getValueMap().get("club", "harborside");
  }

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    final List<KestrosTableHeader> headers = new ArrayList<>();
    final String[] labels = {"#", "", "Name", "Pos", "Age", "Apps", "G", "A"};
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
    if (teamService == null) {
      return rows;
    }
    int i = 0;
    for (final Map<String, String> p
        : teamService.getSquadRows(getClub(), getResource().getPath())) {
      try {
        final List<KestrosTableCell> cells = Arrays.asList(
            rich(p.get("num"), "numbold", null, null, null, p.get("base"), i, 0),
            rich("", "portrait", p.get("href"), p.get("portrait"), p.get("name"), p.get("base"), i, 1),
            rich("", "playerlink", p.get("href"), null, p.get("name"), p.get("base"), i, 2),
            rich(p.get("pos"), "badge", null, null, null, p.get("base"), i, 3),
            cell(p.get("age"), i, 4),
            cell(p.get("apps"), i, 5),
            cell(p.get("goals"), i, 6),
            cell(p.get("assists"), i, 7));
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
      throws ComponentConfigurationException {
    return new SyntheticTableCell(text == null ? "" : text, this, "tableCell",
        "c-" + row + "-" + col);
  }

  private KestrosTableCell rich(final String text, final String layout, final String href,
      final String imageSrc, final String label, final String base, final int row, final int col)
      throws ComponentConfigurationException {
    return new SyntheticRichCell(text == null ? "" : text, layout, href, imageSrc, label, base,
        this, "richCell", "c-" + row + "-" + col);
  }
}
