package io.kestros.samples.acpl.application.components;

import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.samples.acpl.api.services.ScheduleService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Backing model for the ACPL {@code match-filter} component — dropdown options for the client-side
 * results/schedule filter. Thin adapter over {@link ScheduleService}; the node's {@code mode}
 * property scopes the matchweek options ("results" = played weeks, "fixtures" = upcoming).
 */
@Model(adaptables = Resource.class)
public class MatchFilterComponent extends BaseComponent {

  @OSGiService
  @Optional
  private ScheduleService scheduleService;

  /** Clubs for the club dropdown: each entry has {@code slug} + {@code name}. */
  @Nonnull
  public List<Map<String, String>> getClubs() {
    return scheduleService == null ? new ArrayList<>() : scheduleService.getClubOptions();
  }

  /** Matchweek options scoped by the node's {@code mode} property. */
  @Nonnull
  public List<String> getMatchweeks() {
    if (scheduleService == null) {
      return new ArrayList<>();
    }
    return scheduleService.getMatchweekOptions(
        getResource().getValueMap().get("mode", "results"));
  }
}
