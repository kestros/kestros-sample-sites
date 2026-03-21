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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kestros.commons.osgiserviceutils.services.BaseExternalConnectionService;
import io.kestros.samples.fumbbl.core.models.FumbblMatch;
import io.kestros.samples.fumbbl.core.services.FumbblApiClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of FumbblApiClient. Calls the FUMBBL public API to fetch recent
 * Blood Bowl match results. Uses in-memory caching with a 60-second TTL to avoid
 * excessive API calls.
 */
@Component(service = FumbblApiClient.class, immediate = true)
public class DefaultFumbblApiClient extends BaseExternalConnectionService
    implements FumbblApiClient {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultFumbblApiClient.class);

  private static final String FUMBBL_API_URL = "https://fumbbl.com/api/match/list";
  private static final int TIMEOUT_SECONDS = 10;
  private static final long CACHE_TTL_MS = 60_000;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private volatile List<FumbblMatch> cachedMatches = Collections.emptyList();
  private volatile long lastFetchTime = 0;

  @Override
  @Nonnull
  public List<FumbblMatch> getRecentMatches() {
    long now = System.currentTimeMillis();
    if (now - lastFetchTime < CACHE_TTL_MS && !cachedMatches.isEmpty()) {
      return cachedMatches;
    }

    try {
      HttpClient client = HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
          .build();

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(FUMBBL_API_URL))
          .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
          .GET()
          .build();

      HttpResponse<String> response = client.send(request,
          HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        List<FumbblMatch> matches = objectMapper.readValue(response.body(),
            new TypeReference<List<FumbblMatch>>() {});
        cachedMatches = matches;
        lastFetchTime = now;
        connectionSuccessful();
        LOG.info("Fetched {} matches from FUMBBL API", matches.size());
        return matches;
      } else {
        String reason = "FUMBBL API returned HTTP " + response.statusCode();
        LOG.warn(reason);
        connectionFailed(reason);
        return cachedMatches.isEmpty() ? Collections.emptyList() : cachedMatches;
      }
    } catch (IOException e) {
      String reason = "Failed to connect to FUMBBL API: " + e.getMessage();
      LOG.warn(reason);
      connectionFailed(reason);
      return cachedMatches.isEmpty() ? Collections.emptyList() : cachedMatches;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      String reason = "FUMBBL API request interrupted";
      LOG.warn(reason);
      connectionFailed(reason);
      return cachedMatches.isEmpty() ? Collections.emptyList() : cachedMatches;
    }
  }

  @Override
  @Nonnull
  public String getDisplayName() {
    return "FUMBBL API Client";
  }

  @Override
  @Activate
  public void activate(@Nonnull final ComponentContext componentContext) {
    LOG.info("FUMBBL API Client activated");
  }

  @Override
  @Deactivate
  public void deactivate(@Nonnull final ComponentContext componentContext) {
    LOG.info("FUMBBL API Client deactivated");
  }

  @Override
  public void runAdditionalHealthChecks(@Nonnull final FormattingResultLog log) {
    if (getLastSuccessfulConnection() != null) {
      log.info("Last successful FUMBBL API connection: {}",
          getLastSuccessfulConnection());
    } else {
      log.warn("No successful FUMBBL API connection recorded yet");
    }
    if (getLastFailedConnection() != null) {
      log.warn("Last failed FUMBBL API connection: {} - {}",
          getLastFailedConnection(), getLastFailedConnectionReason());
    }
  }
}
