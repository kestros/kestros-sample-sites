/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.samples.fumbbl.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data class representing a single FUMBBL Blood Bowl match result.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FumbblMatch {

  @JsonProperty("id")
  private long id;

  @JsonProperty("date")
  private String date;

  @JsonProperty("time")
  private String time;

  @JsonProperty("division")
  private String division;

  @JsonProperty("team1")
  private Team team1;

  @JsonProperty("team2")
  private Team team2;

  public long getId() {
    return id;
  }

  public String getDate() {
    return date;
  }

  public String getTime() {
    return time;
  }

  public String getDivision() {
    return division;
  }

  public String getTeam1Name() {
    return team1 != null ? team1.name : "";
  }

  public int getTeam1Score() {
    return team1 != null ? team1.score : 0;
  }

  public String getTeam1Roster() {
    return team1 != null && team1.roster != null ? team1.roster.name : "";
  }

  public String getTeam1Coach() {
    return team1 != null && team1.coach != null ? team1.coach.name : "";
  }

  public String getTeam2Name() {
    return team2 != null ? team2.name : "";
  }

  public int getTeam2Score() {
    return team2 != null ? team2.score : 0;
  }

  public String getTeam2Roster() {
    return team2 != null && team2.roster != null ? team2.roster.name : "";
  }

  public String getTeam2Coach() {
    return team2 != null && team2.coach != null ? team2.coach.name : "";
  }

  /**
   * Nested team object from the FUMBBL API response.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Team {
    @JsonProperty("name")
    String name;

    @JsonProperty("score")
    int score;

    @JsonProperty("roster")
    Roster roster;

    @JsonProperty("coach")
    Coach coach;

    @JsonProperty("casualties")
    Casualties casualties;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Roster {
    @JsonProperty("name")
    String name;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Coach {
    @JsonProperty("name")
    String name;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Casualties {
    @JsonProperty("bh")
    int bh;

    @JsonProperty("si")
    int si;

    @JsonProperty("rip")
    int rip;
  }
}
