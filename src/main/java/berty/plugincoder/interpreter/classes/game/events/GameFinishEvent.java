package berty.plugincoder.interpreter.classes.game.events;

import berty.plugincoder.interpreter.classes.game.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import berty.plugincoder.interpreter.classes.game.game.Winner;

public class GameFinishEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Game game;
    private String message="";
    private int time=10;
    private Winner winner;
    private boolean fireworks=true;
    public GameFinishEvent(Game game,Winner winner){
        this.game=game;this.winner=winner;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    public Game getGame() {
        return game;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getTime() {return time;}
    public void setTime(int time) {
        if(time<0)return;
        this.time = time;
    }
    public Winner getWinner() {return winner;}
    public boolean isFireworks() {return fireworks;}
    public void setFireworks(boolean fireworks) {this.fireworks = fireworks;}
}
