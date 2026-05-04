package io.kestros.samples.league.api.models;

import java.util.List;

public interface MerchItem {

    String getId();

    String getName();

    String getCategory();

    String getPrice();

    String getDescription();

    List<String> getTags();
}
