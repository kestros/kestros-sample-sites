package io.kestros.samples.league.api.models;

import java.util.List;

public interface Season {

    String getId();

    String getName();

    int getStartYear();

    int getEndYear();

    List<String> getTeamIds();

    List<String> getMatchIds();
}
