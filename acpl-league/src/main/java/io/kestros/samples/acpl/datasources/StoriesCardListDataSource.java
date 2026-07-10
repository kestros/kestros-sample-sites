package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Latest-news card list, fed from {@link LeagueDataService#getStories()}. Each story becomes a
 * {@link SyntheticStoryCard} rendered by the {@code story-card} card layout. A {@code limit} property
 * trims the list (e.g. 3 on the home widget).
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class StoriesCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private int getLimit() {
    return getResource().getValueMap().get("limit", 0);
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) {
      return cards;
    }
    final String base = siteRoot();
    final String href = base + "/stories.html";
    final List<Map<String, Object>> stories = leagueDataService.getStories();
    final int limit = getLimit();
    int i = 0;
    for (final Map<String, Object> s : stories) {
      if (limit > 0 && i >= limit) {
        break;
      }
      try {
        final String image = base + "/assets/" + str(s.get("image"));
        final String byline = str(s.get("author")) + " · " + formatDate(str(s.get("date")));
        cards.add(new SyntheticStoryCard(
            str(s.get("headline")), str(s.get("dek")), image, href, str(s.get("category")), byline,
            this, "card", "story-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe: skip a story that fails to build
      }
    }
    return cards;
  }

  /** League-site root (e.g. {@code /content/sites/acpl}), from any hosting page. */
  private String siteRoot() {
    final Matcher m = Pattern.compile("^(/content/sites/[^/]+)").matcher(getResource().getPath());
    return m.find() ? m.group(1) : getResource().getPath();
  }

  /** {@code 2026-02-08} → {@code 8 February 2026}; returns the input unchanged if unparseable. */
  private static String formatDate(final String iso) {
    try {
      return java.time.LocalDate.parse(iso)
          .format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale.ENGLISH));
    } catch (final Exception e) {
      return iso;
    }
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
