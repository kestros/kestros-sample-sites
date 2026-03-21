/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.samples.fumbbl.core.models;

import io.kestros.samples.fumbbl.core.services.FumbblApiClient;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Sling model that provides a list of recent FUMBBL Blood Bowl matches for HTL rendering.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class MatchListModel {

  @OSGiService
  private FumbblApiClient fumbblApiClient;

  /**
   * Returns recent FUMBBL matches. Returns an empty list if the API client is unavailable.
   *
   * @return list of recent matches.
   */
  @Nonnull
  public List<FumbblMatch> getMatches() {
    if (fumbblApiClient == null) {
      return Collections.emptyList();
    }
    return fumbblApiClient.getRecentMatches();
  }

  /**
   * Whether matches are available for display.
   *
   * @return true if at least one match is available.
   */
  public boolean getHasMatches() {
    return !getMatches().isEmpty();
  }
}
