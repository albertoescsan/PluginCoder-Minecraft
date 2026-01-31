package berty.plugincoder.interpreter.classes.game.game.team;


import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import berty.plugincoder.interpreter.classes.game.game.Game;

import java.util.ArrayList;
import java.util.List;

public class Teams {

    private List<Team> teams=new ArrayList<>();
    private Scoreboard scoreboard;

    public Teams(Scoreboard scoreboard){
        this.scoreboard=scoreboard;
    }
    public Team createTeam(String name){
        return createTeamFunction(name,1,"§f");
    }
    public Team createTeam(String name,int maxPlayers){
        return createTeamFunction(name,maxPlayers,"§f");
    }
    public Team createTeam(String name,String color){
        return createTeamFunction(name,1,color);
    }
    public Team createTeam(String name,int maxPlayers,String color){
        return createTeamFunction(name,maxPlayers,color);
    }
    private Team createTeamFunction(String name,int maxPlayers,String color){
        if(getTeam(name)!=null)return null;
        Team team=new Team(name,maxPlayers,color,scoreboard);
        teams.add(team);
        return team;
    }
    public Team getTeam(String name){
        return teams.stream().filter(team->team.getName().equals(name)).findFirst().orElse(null);
    }
    public Team getTeam(Player player){
        return teams.stream().filter(team->team.getPlayers().contains(player)).findFirst().orElse(null);
    }
    public List<Team> getList(){
        return teams;
    }
    public void clone(Game game){
        teams.stream().forEach(team->{
            game.getTeams().createTeam(team.getName(),team.getMaxPlayers(),team.getPrefixColor());
            Team newTeam=game.getTeams().getTeam(team.getName());
            newTeam.setEquipment(team.getEquipment());
        });
    }
}
