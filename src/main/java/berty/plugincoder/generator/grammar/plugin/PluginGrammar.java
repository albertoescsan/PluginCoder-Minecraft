package berty.plugincoder.generator.grammar.plugin;

import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Priority(value=0)
public class PluginGrammar {

    public static Plugin plugin;
    @Priority(value=0)
    public static void create(Map<String,String> data, int iteration){
        plugin=new Plugin("");
        plugin.getActivationContainer().add("Activation{}");
        plugin.getDeactivationContainer().add("Deactivation{}");
        String name=data.get("NAME");
        named(name!=null?data:new HashMap<String,String>(){{put("NAME","Plugin");}},iteration);
    }
    @Priority(value=1)
    public static void name(Map<String,String> data,int iteration){
        named(data,iteration);
    }
    @Priority(value=1)
    public static void named(Map<String,String> data,int iteration){
        List<Plugin> plugins=JavaPlugin.getPlugin(PluginCoder.class).getPlugins();
        String name=data.get("NAME");
        int suffix=1;String nameCopy=name;
        while(pluginExistingWithName(plugins,nameCopy)){nameCopy=name+suffix;suffix++;}
        plugin.setName(nameCopy);
    }
    private static boolean pluginExistingWithName(List<Plugin> plugins,String name){
        return plugins.stream().anyMatch(pl->pl.getName().equals(name));
    }

}
