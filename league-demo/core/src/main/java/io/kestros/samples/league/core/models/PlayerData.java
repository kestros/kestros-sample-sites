package io.kestros.samples.league.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.kestros.samples.league.api.models.Player;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerData implements Player {

    private String id;
    private String firstName;
    private String lastName;
    private String position;
    private int number;
    private String teamId;
    private String nationality;
    private String imageUrl;
    private int goals;
    private int assists;
    private int appearances;
    private int yellowCards;
    private int redCards;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }

    @Override public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }

    @Override public int getAppearances() { return appearances; }
    public void setAppearances(int appearances) { this.appearances = appearances; }

    @Override public int getYellowCards() { return yellowCards; }
    public void setYellowCards(int yellowCards) { this.yellowCards = yellowCards; }

    @Override public int getRedCards() { return redCards; }
    public void setRedCards(int redCards) { this.redCards = redCards; }
}
