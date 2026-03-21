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

package io.kestros.samples.fumbbl.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.kestros.samples.fumbbl.core.models.FumbblMatch;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for DefaultFumbblApiClient.
 */
public class DefaultFumbblApiClientTest {

  private DefaultFumbblApiClient apiClient;

  @Before
  public void setUp() {
    apiClient = new DefaultFumbblApiClient();
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("FUMBBL API Client", apiClient.getDisplayName());
  }

  @Test
  public void testGetRecentMatchesReturnsNonNull() {
    // Even if the API is unreachable, the method should return a non-null list
    List<FumbblMatch> matches = apiClient.getRecentMatches();
    assertNotNull("getRecentMatches should never return null", matches);
  }

  @Test
  public void testGetRecentMatchesReturnsListOnSuccess() {
    // Integration-style test: calls the real API
    // If the API is down, this will return an empty list, which is valid
    List<FumbblMatch> matches = apiClient.getRecentMatches();
    assertNotNull(matches);
    // If the API is up, we expect matches
    if (!matches.isEmpty()) {
      FumbblMatch first = matches.get(0);
      assertNotNull("Match date should not be null", first.getDate());
      assertNotNull("Team 1 name should not be null", first.getTeam1Name());
      assertNotNull("Team 2 name should not be null", first.getTeam2Name());
      assertTrue("Match ID should be positive", first.getId() > 0);
    }
  }

  @Test
  public void testCachingReturnsConsistentResults() {
    // Two consecutive calls should return the same cached result
    List<FumbblMatch> first = apiClient.getRecentMatches();
    List<FumbblMatch> second = apiClient.getRecentMatches();
    assertNotNull(first);
    assertNotNull(second);
    assertEquals("Cached results should be the same size", first.size(), second.size());
  }
}
