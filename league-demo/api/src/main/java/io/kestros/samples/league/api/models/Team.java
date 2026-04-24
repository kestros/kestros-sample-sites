package io.kestros.samples.league.api.models;

import java.util.List;

public interface Team {

    String getId();

    String getName();

    String getShortName();

    String getLogoUrl();

    String getCity();

    String getStadium();

    int getFounded();

    List<String> getPlayerIds();
}
