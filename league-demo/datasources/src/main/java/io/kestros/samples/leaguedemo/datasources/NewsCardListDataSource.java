package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
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
public class NewsCardListDataSource extends BaseContainerSlingModelDataSource
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

    if (played.isEmpty()) return cards;

    int lastWeek = played.get(played.size() - 1).getMatchday();

    List<Match> weekMatches = played.stream()
        .filter(m -> m.getMatchday() == lastWeek)
        .collect(Collectors.toList());

    // Find the league leader
    Map<String, int[]> standings = new HashMap<>();
    for (Team t : leagueDataService.getTeams()) {
      standings.put(t.getId(), new int[]{0, 0}); // points, unbeaten
    }
    for (Match m : played) {
      int[] home = standings.get(m.getHomeTeamId());
      int[] away = standings.get(m.getAwayTeamId());
      if (home == null || away == null) continue;
      if (m.getHomeScore() > m.getAwayScore()) {
        home[0] += 3;
      } else if (m.getHomeScore() < m.getAwayScore()) {
        away[0] += 3;
      } else {
        home[0] += 1;
        away[0] += 1;
      }
    }

    String leaderId = null;
    int maxPts = 0;
    for (Map.Entry<String, int[]> e : standings.entrySet()) {
      if (e.getValue()[0] > maxPts) {
        maxPts = e.getValue()[0];
        leaderId = e.getKey();
      }
    }
    Team leader = leaderId != null ? leagueDataService.getTeam(leaderId) : null;

    // Find biggest win of the week
    Match biggestWin = null;
    int biggestMargin = 0;
    for (Match m : weekMatches) {
      int margin = Math.abs(m.getHomeScore() - m.getAwayScore());
      if (margin > biggestMargin) {
        biggestMargin = margin;
        biggestWin = m;
      }
    }

    // Find a high-scoring match
    Match highScoring = null;
    int mostGoals = 0;
    for (Match m : weekMatches) {
      int total = m.getHomeScore() + m.getAwayScore();
      if (total > mostGoals) {
        mostGoals = total;
        highScoring = m;
      }
    }

    int idx = 0;

    // Card 1: Leader headline
    if (leader != null) {
      try {
        cards.add(new KestrosCardImpl(
            leader.getName() + " sit top of the table on " + maxPts
                + " points after " + lastWeek + " matchweeks. Can anyone catch them?",
            new KestrosHeadingImpl(leader.getName() + " Lead the Way",
                "h3", this, "title", "news-title-" + idx),
            null, null, this, "card", "news-" + idx));
        idx++;
      } catch (Exception ignored) {}
    }

    // Card 2: Week recap
    if (!weekMatches.isEmpty()) {
      int totalGoals = weekMatches.stream()
          .mapToInt(m -> m.getHomeScore() + m.getAwayScore()).sum();
      try {
        cards.add(new KestrosCardImpl(
            weekMatches.size() + " matches played, " + totalGoals
                + " goals scored. Here is everything you need to know from Matchweek " + lastWeek + ".",
            new KestrosHeadingImpl("Week " + lastWeek + " Recap: " + totalGoals + " Goals Across " + weekMatches.size() + " Matches",
                "h3", this, "title", "news-title-" + idx),
            null, null, this, "card", "news-" + idx));
        idx++;
      } catch (Exception ignored) {}
    }

    // Card 3: Biggest win
    if (biggestWin != null && biggestMargin > 0) {
      Team home = leagueDataService.getTeam(biggestWin.getHomeTeamId());
      Team away = leagueDataService.getTeam(biggestWin.getAwayTeamId());
      String winner = biggestWin.getHomeScore() > biggestWin.getAwayScore()
          ? (home != null ? home.getName() : biggestWin.getHomeTeamId())
          : (away != null ? away.getName() : biggestWin.getAwayTeamId());
      try {
        cards.add(new KestrosCardImpl(
            winner + " dominated in a "
                + biggestWin.getHomeScore() + "-" + biggestWin.getAwayScore()
                + " result. A clinical performance from start to finish.",
            new KestrosHeadingImpl(winner + " Cruise to Commanding Victory",
                "h3", this, "title", "news-title-" + idx),
            null, null, this, "card", "news-" + idx));
        idx++;
      } catch (Exception ignored) {}
    }

    // Card 4: Season preview
    int remaining = 18 - lastWeek;
    if (remaining > 0) {
      try {
        cards.add(new KestrosCardImpl(
            "With " + remaining + " matchweeks remaining, the battle for the title"
                + " and the fight to avoid the drop are both heating up. Preview the key fixtures ahead.",
            new KestrosHeadingImpl("Looking Ahead: " + remaining + " Weeks to Go",
                "h3", this, "title", "news-title-" + idx),
            null, null, this, "card", "news-" + idx));
        idx++;
      } catch (Exception ignored) {}
    }

    return cards;
  }
}
