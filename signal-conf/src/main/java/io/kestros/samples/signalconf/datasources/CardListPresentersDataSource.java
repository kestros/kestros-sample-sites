package io.kestros.samples.signalconf.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Sling model datasource that renders a Kestros card list for the configured presenters root page.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class CardListPresentersDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  private BaseContentPage rootPage;

  @Nullable
  BaseContentPage getRootPage() {
    if (rootPage == null) {
      String pagesPath = getResource().getValueMap().get("pagesPath", String.class);
      if (pagesPath != null) {
        Resource pageResource = getResourceResolver().getResource(pagesPath);
        if (pageResource != null) {
          rootPage = pageResource.adaptTo(BaseContentPage.class);
        }
      }
    }
    return rootPage;
  }

  /**
   * Read-more text displayed on each card.
   *
   * @return read-more text, or {@code null} if not configured.
   */
  @Nullable
  public String getReadMoreText() {
    return getResource().getValueMap().get("readMoreText", String.class);
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    BaseContentPage root = getRootPage();
    if (root == null) {
      return cards;
    }

    for (BaseContentPage page : root.getChildPages()) {
      try {
        cards.add(
            new KestrosCardImpl(page,
                getReadMoreText(),
                this,
                "card",
                page.getName()));
      } catch (Exception e) {
        // Skip cards that fail to construct — null-safe
      }
    }
    return new ArrayList<>(cards);
  }
}
