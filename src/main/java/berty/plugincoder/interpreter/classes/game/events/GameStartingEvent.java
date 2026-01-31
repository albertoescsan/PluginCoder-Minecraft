package berty.plugincoder.interpreter.classes.game.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import berty.plugincoder.interpreter.classes.game.game.Game;

import java.util.HashMap;
import java.util.Map;

public class GameStartingEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Game game;
    private int time=30;
    private Map<Integer,String> messages=new HashMap<>();
    public GameStartingEvent(Game game){
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

    public Map<Integer, String> getMessages() {
        return messages;
    }

    public int getTime() {return time;}
    public void setTime(int time) {this.time = time;}
}
