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

package io.kestros.samples.fumbbl.core.services;

import io.kestros.samples.fumbbl.core.models.FumbblMatch;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Service for fetching recent match data from the FUMBBL Blood Bowl API.
 */
public interface FumbblApiClient {

  /**
   * Returns a list of recent FUMBBL matches. Returns an empty list (never null)
   * if the API is unreachable.
   *
   * @return list of recent matches, or empty list on failure.
   */
  @Nonnull
  List<FumbblMatch> getRecentMatches();
}
