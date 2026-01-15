package berty.plugincoder.interpreter.translators.server;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.Arrays;
import java.util.List;

public class ServerMethodsTranslator {

    public static void registerMethods(){
        List<String> serverClass= Arrays.asList(Server.class.getTypeName());
        PluginMethod.getMethod("message()").getTranslatedMethodClasses().get("sendMessage").add(ConsoleCommandSender.class.getTypeName());
        PluginMethod console=new PluginMethod("console");
        console.getTranslatedMethodClasses().put("getConsoleSender",serverClass);
        PluginMethod players=new PluginMethod("players");
        players.getTranslatedMethodClasses().put("getOnlinePlayers",serverClass);
        PluginMethod playerServer=new PluginMethod("player()");
        playerServer.getTranslatedMethodClasses().put("getPlayer",serverClass);
        PluginMethod worldServer=new PluginMethod("world()");
        worldServer.getTranslatedMethodClasses().put("getWorld",serverClass);
    }
}
