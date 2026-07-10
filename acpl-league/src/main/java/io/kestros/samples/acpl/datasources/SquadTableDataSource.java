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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Club squad table, fed from {@link LeagueDataService#getSquad(String)}. The club is taken from a
 * {@code club} property on the table (e.g. {@code club="harborside"}). Rows match the static mock:
 * bold shirt number, linked player portrait, player-name link, position badge, then age/apps/G/A.
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
    if (leagueDataService == null) {
      return rows;
    }
    final String base = siteRoot();
    final String href = base + "/player.html";
    int i = 0;
    for (final Map<String, Object> p : leagueDataService.getSquad(getClub())) {
      try {
        final Map<String, Object> season = asMap(p.get("season"));
        final String name = str(p.get("name"));
        final List<KestrosTableCell> cells = Arrays.asList(
            rich(str(p.get("num")), "numbold", null, null, null, i, 0),
            rich("", "portrait", href, portraitFor(name), name, i, 1),
            rich("", "playerlink", href, null, name, i, 2),
            rich(str(p.get("pos")), "badge", null, null, null, i, 3),
            cell(str(p.get("age")), i, 4),
            cell(str(season.get("apps")), i, 5),
            cell(str(season.get("goals")), i, 6),
            cell(str(season.get("assists")), i, 7));
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
    return new SyntheticTableCell(text == null ? "" : text, this, "tableCell", "c-" + row + "-" + col);
  }

  /** Deterministic placeholder portrait (1 of 20 uniform avatars) for a player, keyed by name. */
  private String portraitFor(final String name) {
    final int idx = Math.abs(name.hashCode()) % 20 + 1;
    return siteRoot() + "/assets/portraits/p" + idx + ".svg";
  }

  private KestrosTableCell rich(final String text, final String layout, final String href,
      final String imageSrc, final String label, final int row, final int col)
      throws ComponentConfigurationException {
    return new SyntheticRichCell(text == null ? "" : text, layout, href, imageSrc, label, siteRoot(),
        this, "richCell", "c-" + row + "-" + col);
  }

  /** League-site root (e.g. {@code /content/sites/acpl}), from any hosting page. */
  private String siteRoot() {
    final Matcher m = Pattern.compile("^(/content/sites/[^/]+)").matcher(getResource().getPath());
    return m.find() ? m.group(1) : getResource().getPath();
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> asMap(final Object o) {
    return o instanceof Map ? (Map<String, Object>) o : new java.util.HashMap<>();
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
