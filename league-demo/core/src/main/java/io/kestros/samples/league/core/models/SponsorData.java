package io.kestros.samples.league.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.kestros.samples.league.api.models.Sponsor;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SponsorData implements Sponsor {

    private String id;
    private String name;
    private String tier;
    private String category;
    private String website;
    private String description;
    private String logoUrl;

    @Override public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    @Override public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    @Override public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}
