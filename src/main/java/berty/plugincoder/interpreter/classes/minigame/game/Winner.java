package berty.plugincoder.interpreter.classes.minigame.game;

import org.bukkit.entity.Player;
import berty.plugincoder.interpreter.classes.minigame.game.team.Team;

import java.util.Arrays;
import java.util.List;

public class Winner {
    private Player player;
    private Team team;
    public Winner(Player player){
        this.player=player;
    }
    public Winner(Team team){
        this.team=team;
    }

    public String getName() {
        if(player!=null)return player.getName();
        return team.getName();
    }

    public Player getPlayer() {
        return player;
    }
    public List<Player> getPlayers() {
        if(player!=null)return Arrays.asList(player);
        return team.getPlayers();
    }
    public Team getTeam() {
        return team;
    }
}
