package berty.plugincoder.interpreter.classes.minigame.events;

import berty.plugincoder.interpreter.classes.minigame.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveGameEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Game game;
    private Player player;
    private String message="";
    private boolean cancelled=false;
    public PlayerLeaveGameEvent(Game game, Player player){
        this.game=game;this.player=player;
    }
    @Override
    public HandlerList getHandlers() {return HANDLERS;}
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    public Game getGame() {
        return game;
    }
    public Player getPlayer() {
        return player;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
