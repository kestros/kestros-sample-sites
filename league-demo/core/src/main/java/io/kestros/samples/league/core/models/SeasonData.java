package io.kestros.samples.league.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.kestros.samples.league.api.models.Season;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SeasonData implements Season {

    private String id;
    private String name;
    private int startYear;
    private int endYear;
    private List<String> teamIds = new ArrayList<>();
    private List<String> matchIds = new ArrayList<>();
    private String championId;
    private String runnerUpId;
    private String topScorerId;
    private int topScorerGoals;
    private String status;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    @Override
    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    @Override
    public List<String> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(List<String> teamIds) {
        this.teamIds = teamIds;
    }

    @Override
    public List<String> getMatchIds() {
        return matchIds;
    }

    public void setMatchIds(List<String> matchIds) {
        this.matchIds = matchIds;
    }

    @Override
    public String getChampionId() { return championId; }
    public void setChampionId(String championId) { this.championId = championId; }

    @Override
    public String getRunnerUpId() { return runnerUpId; }
    public void setRunnerUpId(String runnerUpId) { this.runnerUpId = runnerUpId; }

    @Override
    public String getTopScorerId() { return topScorerId; }
    public void setTopScorerId(String topScorerId) { this.topScorerId = topScorerId; }

    @Override
    public int getTopScorerGoals() { return topScorerGoals; }
    public void setTopScorerGoals(int topScorerGoals) { this.topScorerGoals = topScorerGoals; }

    @Override
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
