package io.kestros.samples.league.api.models;

import java.util.List;

public interface Article {

    String getId();

    String getTitle();

    String getSummary();

    String getDate();

    String getCategory();

    List<String> getTags();

    String getImageUrl();
}
