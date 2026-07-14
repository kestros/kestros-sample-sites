package io.kestros.samples.acpl.core.services;

import io.kestros.samples.acpl.api.services.LeagueDataService;
import io.kestros.samples.acpl.api.services.MatchService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Display-ready match data, backed by {@link LeagueDataService}. */
@Component(service = MatchService.class, immediate = true)
public class MatchServiceImpl extends AbstractDisplayService implements MatchService {

  @Reference
  private LeagueDataService leagueDataService;

  private Map<String, Object> match(final String matchId) {
    return matchId == null || matchId.isEmpty()
        ? leagueDataService.getFeaturedMatch()
        : leagueDataService.getMatch(matchId);
  }

  @Nonnull
  @Override
  public Map<String, String> getMatchHeader(final String matchId, final String contextPath) {
    final Map<String, String> header = new LinkedHashMap<>();
    final Map<String, Object> m = match(matchId);
    if (m == null || m.isEmpty()) {
      return header;
    }
    final String home = str(m.get("home"));
    final String away = str(m.get("away"));
    header.put("homeSlug", home);
    header.put("homeName", leagueDataService.getClubName(home));
    header.put("awaySlug", away);
    header.put("awayName", leagueDataService.getClubName(away));
    final boolean played = Boolean.TRUE.equals(m.get("played"));
    header.put("played", played ? "true" : "false");
    header.put("score", played ? str(m.get("hg")) + " – " + str(m.get("ag")) : "vs");
    final StringBuilder meta = new StringBuilder();
    if (!str(m.get("mw")).isEmpty()) {
      meta.append("Matchweek ").append(str(m.get("mw")));
    }
    if (!str(m.get("date")).isEmpty()) {
      meta.append(meta.length() > 0 ? " · " : "").append(formatDateLong(str(m.get("date"))));
    }
    if (!str(m.get("venue")).isEmpty()) {
      meta.append(meta.length() > 0 ? " · " : "").append(str(m.get("venue")));
    }
    if (!played && !str(m.get("kickoff")).isEmpty()) {
      meta.append(meta.length() > 0 ? " · " : "").append("Kick-off ").append(str(m.get("kickoff")));
    }
    if (played) {
      if (!str(m.get("referee")).isEmpty()) {
        meta.append(meta.length() > 0 ? " · " : "").append("Referee: ").append(str(m.get("referee")));
      }
      final int att = asInt(m.get("attendance"));
      if (att > 0) {
        meta.append(meta.length() > 0 ? " · " : "")
            .append("Attendance: ").append(String.format("%,d", att));
      }
    }
    header.put("meta", meta.toString());
    header.put("base", siteRoot(contextPath));
    if (played) {
      // link the editorial report when one exists (report slugs are mw{n}-{winner}-{loser})
      final String slugA = "mw" + str(m.get("mw")) + "-" + home + "-" + away;
      final String slugB = "mw" + str(m.get("mw")) + "-" + away + "-" + home;
      for (final Map<String, Object> story : leagueDataService.getStories()) {
        final String slug = str(story.get("slug"));
        if (slug.equals(slugA) || slug.equals(slugB)) {
          header.put("reportHref", siteRoot(contextPath) + "/stories/" + slug + ".html");
          break;
        }
      }
    }
    return header;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public List<Map<String, String>> getGoalRows(final String matchId, final String contextPath) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final Map<String, Object> m = match(matchId);
    if (m == null || !(m.get("goals") instanceof List)) {
      return rows;
    }
    final String base = siteRoot(contextPath);
    for (final Map<String, Object> g : (List<Map<String, Object>>) m.get("goals")) {
      final Map<String, String> row = new LinkedHashMap<>();
      final String scorerSlug = str(g.get("scorerSlug"));
      row.put("minute", str(g.get("minute")));
      row.put("scorer", str(g.get("scorer")));
      row.put("href", scorerSlug.isEmpty() ? "" : base + "/players/" + scorerSlug + ".html");
      row.put("teamSlug", str(g.get("team")));
      row.put("side", str(g.get("team")).equals(str(m.get("home"))) ? "home" : "away");
      row.put("assist", str(g.get("assist")));
      row.put("base", base);
      rows.add(row);
    }
    return rows;
  }

  @Nonnull
  @Override
  public Map<String, String> getPreviewInfo(final String matchId, final String contextPath) {
    final Map<String, String> info = new LinkedHashMap<>();
    final Map<String, Object> m = match(matchId);
    if (m == null || m.isEmpty()) {
      return info;
    }
    final String base = siteRoot(contextPath);
    final String home = str(m.get("home"));
    final String away = str(m.get("away"));
    info.put("base", base);
    info.put("homeSlug", home);
    info.put("awaySlug", away);
    info.put("date", formatDateLong(str(m.get("date"))));
    info.put("kickoff", str(m.get("kickoff")));
    info.put("venue", str(m.get("venue")));
    info.put("referee", str(m.get("referee")));
    info.put("homeName", leagueDataService.getClubName(home));
    info.put("awayName", leagueDataService.getClubName(away));
    info.put("homePos", ordinal(asInt(leagueDataService.getStanding(home).get("pos"))));
    info.put("awayPos", ordinal(asInt(leagueDataService.getStanding(away).get("pos"))));
    info.put("homeForm", formString(home));
    info.put("awayForm", formString(away));
    // link the editorial preview story when one exists for this fixture
    final String previewSlug = "preview-mw" + str(m.get("mw")) + "-" + home + "-" + away;
    for (final Map<String, Object> story : leagueDataService.getStories()) {
      if (previewSlug.equals(str(story.get("slug")))) {
        info.put("previewHref", base + "/stories/" + previewSlug + ".html");
        break;
      }
    }
    return info;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getHeadToHeadRows(final String matchId,
      final String contextPath) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final Map<String, Object> m = match(matchId);
    if (m == null || m.isEmpty()) {
      return rows;
    }
    final String base = siteRoot(contextPath);
    for (final Map<String, Object> meeting
        : leagueDataService.getMeetings(str(m.get("home")), str(m.get("away")), 6)) {
      if (str(meeting.get("id")).equals(str(m.get("id")))) {
        // the match being viewed is not one of its own "recent meetings"
        continue;
      }
      if (rows.size() >= 5) {
        break;
      }
      final String home = str(meeting.get("home"));
      final String away = str(meeting.get("away"));
      final Map<String, String> row = new LinkedHashMap<>();
      row.put("homeSlug", home);
      row.put("homeShort", leagueDataService.getClubShort(home));
      row.put("homeName", leagueDataService.getClubName(home));
      row.put("awaySlug", away);
      row.put("awayShort", leagueDataService.getClubShort(away));
      row.put("awayName", leagueDataService.getClubName(away));
      row.put("mid", str(meeting.get("hg")) + " – " + str(meeting.get("ag")));
      // only current-season meetings have live match pages
      final String season = str(meeting.get("season"));
      row.put("href", season.equals("2025-26")
          ? base + "/matches/" + str(meeting.get("id")) + ".html" : "");
      row.put("subText", "MW" + str(meeting.get("mw")) + " · " + season);
      row.put("base", base);
      rows.add(row);
    }
    return rows;
  }

  /** Last-5 form as a compact string, e.g. "W W D W L". */
  private String formString(final String club) {
    final StringBuilder out = new StringBuilder();
    for (final Map<String, Object> fRow : leagueDataService.getClubForm(club, 5)) {
      if (out.length() > 0) {
        out.append(' ');
      }
      out.append(str(fRow.get("result")));
    }
    return out.toString();
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public List<Map<String, String>> getMatchStatRows(final String matchId) {
    final List<Map<String, String>> rows = new ArrayList<>();
    final Map<String, Object> m = match(matchId);
    if (m == null || !(m.get("stats") instanceof List)) {
      return rows;
    }
    for (final Map<String, Object> s : (List<Map<String, Object>>) m.get("stats")) {
      final Map<String, String> row = new LinkedHashMap<>();
      row.put("h", str(s.get("h")));
      row.put("label", str(s.get("label")));
      row.put("a", str(s.get("a")));
      final int hv = asInt(str(s.get("h")).replace("%", ""));
      final int av = asInt(str(s.get("a")).replace("%", ""));
      final int hPct = (hv + av) == 0 ? 50 : Math.round(100f * hv / (hv + av));
      row.put("hPct", String.valueOf(hPct));
      row.put("aPct", String.valueOf(100 - hPct));
      rows.add(row);
    }
    return rows;
  }
}
