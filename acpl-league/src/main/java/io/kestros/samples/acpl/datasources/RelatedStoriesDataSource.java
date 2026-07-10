package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.acpl.services.LeagueDataService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Related-stories card list for a story page. Reads the current story slug from a {@code story}
 * property and returns other stories that involve at least one of the same clubs, as
 * {@link SyntheticStoryCard}s rendered by the {@code story-card} layout. {@code limit} caps the count.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class RelatedStoriesDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @Optional
  private LeagueDataService leagueDataService;

  private String getCurrentStory() {
    return getResource().getValueMap().get("story", "");
  }

  private int getLimit() {
    return getResource().getValueMap().get("limit", 3);
  }

  private Set<String> clubSlugs() {
    final Set<String> slugs = new LinkedHashSet<>();
    if (leagueDataService != null) {
      for (final Map<String, Object> r : leagueDataService.getStandings()) {
        slugs.add(str(r.get("club")));
      }
    }
    return slugs;
  }

  /** Club slugs mentioned in a story slug (mw&lt;N&gt;-&lt;home&gt;-&lt;away&gt;). */
  private Set<String> teamsIn(final String storySlug, final Set<String> clubs) {
    final Set<String> teams = new LinkedHashSet<>();
    final String rest = storySlug.contains("-") ? storySlug.substring(storySlug.indexOf('-') + 1)
        : storySlug;
    for (final String c : clubs) {
      if (rest.contains(c)) {
        teams.add(c);
      }
    }
    return teams;
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) {
      return cards;
    }
    final String current = getCurrentStory();
    final Set<String> clubs = clubSlugs();
    final Set<String> currentTeams = teamsIn(current, clubs);
    final String base = siteRoot();
    final int limit = getLimit();

    // Prefer stories sharing a club with the current one, then fill with other recent stories, so
    // the Related Stories section is never empty (e.g. features/analysis with no club in the slug).
    final List<Map<String, Object>> matched = new ArrayList<>();
    final List<Map<String, Object>> others = new ArrayList<>();
    for (final Map<String, Object> s : leagueDataService.getStories()) {
      final String slug = str(s.get("slug"));
      if (slug.equals(current)) {
        continue;
      }
      final Set<String> t = teamsIn(slug, clubs);
      t.retainAll(currentTeams);
      if (!currentTeams.isEmpty() && !t.isEmpty()) {
        matched.add(s);
      } else {
        others.add(s);
      }
    }
    final List<Map<String, Object>> selected = new ArrayList<>(matched);
    selected.addAll(others);

    int i = 0;
    for (final Map<String, Object> s : selected) {
      if (i >= limit) {
        break;
      }
      try {
        final String slug = str(s.get("slug"));
        final String image = base + "/assets/" + str(s.get("image"));
        final String href = base + "/stories/" + slug + ".html";
        final String byline = str(s.get("author")) + " · " + formatDate(str(s.get("date")));
        cards.add(new SyntheticStoryCard(str(s.get("headline")), str(s.get("dek")), image, href,
            str(s.get("category")), byline, this, "card", "rel-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe: skip a story that fails to build
      }
    }
    return cards;
  }

  private String siteRoot() {
    final Matcher m = Pattern.compile("^(/content/sites/[^/]+)").matcher(getResource().getPath());
    return m.find() ? m.group(1) : getResource().getPath();
  }

  private static String formatDate(final String iso) {
    try {
      return java.time.LocalDate.parse(iso)
          .format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy",
              java.util.Locale.ENGLISH));
    } catch (final Exception e) {
      return iso;
    }
  }

  private static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }
}
