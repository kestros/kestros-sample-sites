package io.kestros.samples.acpl.core.services;

import io.kestros.samples.acpl.api.services.LeagueDataService;
import io.kestros.samples.acpl.api.services.StandingsService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Display-ready standings data, backed by {@link LeagueDataService}. */
@Component(service = StandingsService.class, immediate = true)
public class StandingsServiceImpl extends AbstractDisplayService implements StandingsService {

  @Reference
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<Map<String, Object>> getStandingsRows(final int limit) {
    final List<Map<String, Object>> all = leagueDataService.getStandings();
    if (limit > 0 && limit < all.size()) {
      return all.subList(0, limit);
    }
    return all;
  }

  @Nonnull
  @Override
  public List<Map<String, Object>> getStandingsRowsForClubs(final List<String> clubs) {
    final List<Map<String, Object>> rows = new ArrayList<>();
    for (final Map<String, Object> row : leagueDataService.getStandings()) {
      if (clubs.contains(String.valueOf(row.get("club")))) {
        rows.add(row);
      }
    }
    return rows;
  }

  @Nonnull
  @Override
  public String getZone(final int pos) {
    if (pos <= 0) {
      return "";
    }
    if (pos <= 3) {
      return "qualify";
    }
    final int total = leagueDataService.getStandings().size();
    if (pos > total - 2) {
      return "relegation";
    }
    return "";
  }

  @Nonnull
  @Override
  public Map<String, String> getClubCell(final String slug, final String displayMode,
      final String contextPath) {
    final Map<String, String> cell = new LinkedHashMap<>();
    final boolean useName = "rich-name".equals(displayMode);
    cell.put("slug", slug);
    cell.put("label", useName
        ? leagueDataService.getClubName(slug)
        : leagueDataService.getClubShort(slug));
    cell.put("linkClass", useName ? "fw-semibold club-link" : "club-link");
    cell.put("base", siteRoot(contextPath));
    return cell;
  }
}
