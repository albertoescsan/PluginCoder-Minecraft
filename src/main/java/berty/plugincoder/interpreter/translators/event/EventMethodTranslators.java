package berty.plugincoder.interpreter.translators.event;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.ArrayList;
import java.util.Arrays;

public class EventMethodTranslators {

    public static void registerMethods(){
        PluginMethod player=new PluginMethod("player");
        //TODO añadir mas eventos con getPlayer
        player.getTranslatedMethodClasses().put("getPlayer", new ArrayList<>(Arrays.asList(
                PlayerJoinEvent.class.getTypeName(), PlayerQuitEvent.class.getTypeName(),
                PlayerMoveEvent.class.getTypeName(), BlockPlaceEvent.class.getTypeName(),
                BlockBreakEvent.class.getTypeName(), PlayerTeleportEvent.class.getTypeName(),
                PlayerInteractEvent.class.getTypeName(), PlayerDeathEvent.class.getTypeName(),
                PlayerChatEvent.class.getTypeName()
        )));
        player.getTranslatedMethodClasses().put("getEntity",new ArrayList<>(Arrays.asList(PlayerDeathEvent.class.getTypeName())));
        PluginMethod getMessage=new PluginMethod("message");
        getMessage.getTranslatedMethodClasses().put("getMessage",new ArrayList<>(Arrays.asList(PlayerChatEvent.class.getTypeName())));
    }
}
