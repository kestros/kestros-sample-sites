package io.kestros.samples.league.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.kestros.samples.league.api.models.Article;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleData implements Article {

    private String id;
    private String title;
    private String summary;
    private String date;
    private String category;
    private List<String> tags = new ArrayList<>();
    private String imageUrl;

    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    @Override
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    @Override
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
