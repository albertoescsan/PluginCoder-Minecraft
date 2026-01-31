package berty.plugincoder.interpreter.classes.rank;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class Rank {

    private String name;
    private List<Player> players=new ArrayList<>();
    private Team team;
    public Rank(String name){

    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
