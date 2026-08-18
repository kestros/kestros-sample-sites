package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;

/**
 * Shared base for the ACPL card-list datasources. Datasources are thin adapters: they read node
 * properties / route params here, call a domain service for display-ready data, and wrap the result
 * into synthetic resources — no computation of their own.
 */
public abstract class AbstractLeagueCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  /**
   * Value of a route parameter: the {@code name} request attribute set by a dynamic-page filter, else
   * the same-named node property, else {@code defaultValue}.
   */
  protected String getParam(final String name, final String defaultValue) {
    if (getRequest() != null && getRequest().getAttribute(name) != null) {
      final String value = String.valueOf(getRequest().getAttribute(name));
      if (!value.isEmpty()) {
        return value;
      }
    }
    return getResource().getValueMap().get(name, defaultValue);
  }

  /** Club slug from the {@code team} route param, else the node's {@code club} property. */
  protected String getTeam() {
    if (getRequest() != null && getRequest().getAttribute("team") != null) {
      final String team = String.valueOf(getRequest().getAttribute("team"));
      if (!team.isEmpty()) {
        return team;
      }
    }
    return getResource().getValueMap().get("club", "harborside");
  }

  /** This datasource's content path — passed to services so they can derive the site root. */
  protected String contextPath() {
    return getResource().getPath();
  }
}
