package berty.plugincoder.interpreter.listener;

import java.util.HashMap;
import java.util.Map;

import berty.plugincoder.interpreter.classes.game.events.*;
import berty.plugincoder.interpreter.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;

public class PluginListener implements Listener {

	private PluginCoder mainPlugin;
	public PluginListener(PluginCoder pluginCoder) {
		mainPlugin=pluginCoder;
	}

	private void executeListener(Object event,String eventName) {
		for(Plugin plugin: mainPlugin.getPlugins()){
			if(!plugin.isEnabled())continue;
			Map<String,Object> inicVars=mainPlugin.getPluginVars(plugin);
			inicVars.put("event", event);
			plugin.getListener().stream().filter(function->function.matches("^"+eventName+"\\s*\\{(.+)$")).forEach(function->{
				//function,Map of inicVars and names
				mainPlugin.getCodeExecuter().executeFunction(function,"", new HashMap<>(inicVars));
				if(PluginCoder.isErrorFound())ErrorManager.errorInListener(eventName);
			});
		}
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {this.executeListener(event, "onPlayerJoin");}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		this.executeListener(event, "onPlayerLeave");
	}
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event) {
		this.executeListener(event, "onPlayerClick");
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		this.executeListener(event, "onPlayerMove");
	}
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {this.executeListener(event, "onPlayerTeleport");}
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {this.executeListener(event, "onPlayerChat");}
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent event) {this.executeListener(event, "onPlayerDie");}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		this.executeListener(event, "onBlockPlace");
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		this.executeListener(event, "onBlockBreak");
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		this.executeListener(event, "onInventoryClick");
	}
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {this.executeListener(event, "onEntityDamage");}


	@EventHandler
	public void onPlayerJoinGame(PlayerJoinGameEvent event) {this.executeListener(event, "onPlayerJoinGame");}
	@EventHandler
	public void onPlayerLeaveGame(PlayerLeaveGameEvent event) {this.executeListener(event, "onPlayerLeaveGame");}
	@EventHandler
	public void onGameStarting(GameStartingEvent event) {this.executeListener(event, "onGameStarting");}
	@EventHandler
	public void onGameStart(GameStartEvent event) {this.executeListener(event, "onGameStart");}
	@EventHandler
	public void onGameFinish(GameFinishEvent event) {this.executeListener(event, "onGameFinish");}
}
