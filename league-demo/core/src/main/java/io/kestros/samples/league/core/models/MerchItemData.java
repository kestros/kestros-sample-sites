package io.kestros.samples.league.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.kestros.samples.league.api.models.MerchItem;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchItemData implements MerchItem {

    private String id;
    private String name;
    private String category;
    private String price;
    private String description;
    private List<String> tags = new ArrayList<>();

    @Override public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    @Override public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
