package io.kestros.samples.league.api.models;

public interface Match {

    String getId();

    String getHomeTeamId();

    String getAwayTeamId();

    int getHomeScore();

    int getAwayScore();

    int getMatchday();

    String getSeasonId();

    String getDate();

    String getVenue();

    boolean isPlayed();
}
