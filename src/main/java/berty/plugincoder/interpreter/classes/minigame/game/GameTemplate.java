package berty.plugincoder.interpreter.classes.minigame.game;

import berty.plugincoder.interpreter.classes.scoreboard.Scoreboard;
import berty.plugincoder.interpreter.classes.minigame.team.Teams;

public class GameTemplate {
    private Scoreboard scoreboard=new Scoreboard("");
    private Teams teams;

    private int minPlayers=2;
    private int maxPlayers=2;

    public GameTemplate(){
        teams=new Teams(scoreboard.getBukkitScoreboard());
    }
    public Game build(String name){
        Game game=new Game(name);
        teams.clone(game);
        scoreboard.clone(game.getScoreboard());
        game.setMaxPlayers(maxPlayers);
        game.setMinPlayers(minPlayers);
        return game;
    }
    public Teams getTeams() {
        return teams;
    }
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
