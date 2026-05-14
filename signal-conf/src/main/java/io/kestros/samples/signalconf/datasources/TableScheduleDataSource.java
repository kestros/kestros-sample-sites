package io.kestros.samples.signalconf.datasources;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.tagging.api.models.KestrosTag;
import io.kestros.cms.tagging.api.services.TagRetrievalService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Sling model datasource that renders the conference schedule for a configured day as a Kestros
 * table.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class TableScheduleDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private TagRetrievalService tagRetrievalService;

  @Nullable
  String getDayTag() {
    return getResource().getValueMap().get("dayTag", String.class);
  }

  @Nullable
  String getSessionsPath() {
    return getResource().getValueMap().get("sessionsPath", String.class);
  }

  @Nonnull
  List<BaseContentPage> getSessionsForDay() {
    List<BaseContentPage> sessions = new ArrayList<>();
    String dayTag = getDayTag();
    String sessionsPath = getSessionsPath();

    if (dayTag == null || sessionsPath == null || tagRetrievalService == null) {
      return sessions;
    }

    Resource sessionsResource = getResourceResolver().getResource(sessionsPath);
    if (sessionsResource == null) {
      return sessions;
    }

    BaseContentPage sessionsPage = sessionsResource.adaptTo(BaseContentPage.class);
    if (sessionsPage == null) {
      return sessions;
    }

    for (BaseContentPage sessionPage : sessionsPage.getChildPages()) {
      List<KestrosTag> tags = tagRetrievalService.getTagsOnResource(sessionPage.getResource());
      for (KestrosTag tag : tags) {
        if (dayTag.equals(tag.getPath())) {
          sessions.add(sessionPage);
          break;
        }
      }
    }
    return sessions;
  }

  @Nonnull
  @Override
  @SuppressFBWarnings({"DE_MIGHT_IGNORE", "REC_CATCH_EXCEPTION"})
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("Time", this, "header", "time-header"));
      headers.add(new SyntheticTableHeader("Session", this, "header", "session-header"));
      headers.add(new SyntheticTableHeader("Presenter", this, "header", "presenter-header"));
    } catch (Exception e) {
      // Return empty headers on error — null-safe
    }
    return headers;
  }

  @Nonnull
  @Override
  @SuppressFBWarnings({"DE_MIGHT_IGNORE", "REC_CATCH_EXCEPTION"})
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    int rowIndex = 0;
    for (BaseContentPage session : getSessionsForDay()) {
      try {
        String time = session.getProperty("sessionTime", "");
        String title = session.getDisplayTitle();
        String presenter = session.getProperty("presenterName", "");

        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(time, this, "cell", "time-" + rowIndex),
            new SyntheticTableCell(title, this, "cell", "session-" + rowIndex),
            new SyntheticTableCell(presenter, this, "cell", "presenter-" + rowIndex)
        );

        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + rowIndex));
        rowIndex++;
      } catch (Exception e) {
        // Skip rows that fail to construct — null-safe
      }
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
