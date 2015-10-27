package barqsoft.footballscores.data;

/**
 * Created by Vazh on 26/10/2015.
 */
public class Match {

    String homeTeam;
    String awayTeam;
    String homeGoals;
    String awayGoals;
    String matchDay;

    public Match(String homeTeam, String awayTeam, String homeGoals, String awayGoals, String matchDay) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        if (homeGoals.equals("-1")) {
            homeGoals = "0";
        }
        this.homeGoals = homeGoals;
        if (awayGoals.equals("-1")) {
            awayGoals = "0";
        }
        this.awayGoals = awayGoals;
        this.matchDay = matchDay;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(String homeGoals) {
        this.homeGoals = homeGoals;
    }

    public String getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(String awayGoals) {
        this.awayGoals = awayGoals;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
    }
}
