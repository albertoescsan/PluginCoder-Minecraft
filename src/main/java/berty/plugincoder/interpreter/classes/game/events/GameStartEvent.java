package berty.plugincoder.interpreter.classes.game.events;

import berty.plugincoder.interpreter.classes.game.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Game game;
    private String message="";
    public GameStartEvent(Game game){
        this.game=game;
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
}
