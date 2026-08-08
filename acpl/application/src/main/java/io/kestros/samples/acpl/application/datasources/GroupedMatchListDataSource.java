package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.ScheduleService;
import java.util.ArrayList;
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
 * Results / fixtures grouped by matchweek ({@code mode} property), ascending. Thin adapter over
 * {@link ScheduleService}; each group is a {@link SyntheticMatchweekGroup} rendered by the
 * {@code results-group} / {@code fixtures-group} card layouts.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class GroupedMatchListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(GroupedMatchListDataSource.class);

  @OSGiService
  @Optional
  private ScheduleService scheduleService;

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> groups = new ArrayList<>();
    if (scheduleService == null) {
      return groups;
    }
    final boolean fixtures = "fixtures".equals(getResource().getValueMap().get("mode", "results"));
    final String layout = fixtures ? "fixtures-group" : "results-group";
    final List<Map<String, Object>> weeks = fixtures
        ? scheduleService.getFixturesByMatchweek(contextPath())
        : scheduleService.getResultsByMatchweek(contextPath());
    int i = 0;
    for (final Map<String, Object> week : weeks) {
      try {
        groups.add(new SyntheticMatchweekGroup(layout, String.valueOf(week.get("matchweek")),
            String.valueOf(week.get("base")), "",
            (List<Map<String, String>>) week.get("matches"), this, "card", "mw-" + i));
        i++;
      } catch (final Exception e) {
        LOG.error("GroupedMatchListDataSource: {}", e.getMessage());
        // null-safe: skip a group that fails to build
      }
    }
    return groups;
  }
}
