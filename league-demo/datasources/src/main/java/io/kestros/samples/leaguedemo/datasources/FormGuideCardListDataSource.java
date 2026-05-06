package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.cms.components.basic.core.content.image.KestrosImageImpl;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class FormGuideCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    List<Match> played = leagueDataService.getMatches().stream()
        .filter(Match::isPlayed)
        .collect(Collectors.toList());

    // Build form for each team (last 5 matches)
    Map<String, List<String>> formMap = new HashMap<>();
    Map<String, int[]> pointsMap = new HashMap<>();
    for (Team t : leagueDataService.getTeams()) {
      formMap.put(t.getId(), new ArrayList<>());
      pointsMap.put(t.getId(), new int[]{0}); // points from last 5
    }

    // Process matches in order, keeping only last 5 per team
    for (Match m : played) {
      String homeId = m.getHomeTeamId();
      String awayId = m.getAwayTeamId();
      if (m.getHomeScore() > m.getAwayScore()) {
        formMap.computeIfAbsent(homeId, k -> new ArrayList<>()).add("W");
        formMap.computeIfAbsent(awayId, k -> new ArrayList<>()).add("L");
      } else if (m.getHomeScore() < m.getAwayScore()) {
        formMap.computeIfAbsent(homeId, k -> new ArrayList<>()).add("L");
        formMap.computeIfAbsent(awayId, k -> new ArrayList<>()).add("W");
      } else {
        formMap.computeIfAbsent(homeId, k -> new ArrayList<>()).add("D");
        formMap.computeIfAbsent(awayId, k -> new ArrayList<>()).add("D");
      }
    }

    // Trim to last 5 and calculate points
    List<Map.Entry<String, Integer>> sorted = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : formMap.entrySet()) {
      List<String> form = entry.getValue();
      int start = Math.max(0, form.size() - 5);
      List<String> last5 = form.subList(start, form.size());
      formMap.put(entry.getKey(), last5);

      int pts = 0;
      for (String r : last5) {
        if ("W".equals(r)) pts += 3;
        else if ("D".equals(r)) pts += 1;
      }
      sorted.add(Map.entry(entry.getKey(), pts));
    }

    // Sort by form points (best form first)
    sorted.sort((a, b) -> b.getValue() - a.getValue());

    int i = 0;
    for (Map.Entry<String, Integer> entry : sorted) {
      String teamId = entry.getKey();
      Team team = leagueDataService.getTeam(teamId);
      if (team == null) continue;
      List<String> form = formMap.get(teamId);

      String formStr = String.join(" ", form);
      int wins = (int) form.stream().filter("W"::equals).count();
      int draws = (int) form.stream().filter("D"::equals).count();
      int losses = (int) form.stream().filter("L"::equals).count();
      int pts = entry.getValue();

      try {
        String desc = "Last 5: " + formStr + " | " + wins + "W " + draws + "D " + losses + "L"
            + " | " + pts + " pts from 5 matches";

        KestrosImage crest = null;
        if (team.getLogoUrl() != null && !team.getLogoUrl().isEmpty()) {
          try {
            crest = new KestrosImageImpl(
                team.getLogoUrl(), team.getName() + " crest", null, null,
                null, null, null, AnchorTarget.SAME_WINDOW,
                this, "image", "imageElement", null);
          } catch (Exception ignored) {}
        }

        cards.add(new KestrosCardImpl(
            desc,
            new KestrosHeadingImpl((i + 1) + ". " + team.getName(),
                "h3", this, "title", "titleElement"),
            crest, null, this, "card", "form-" + teamId));
        i++;
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
