package berty.plugincoder.interpreter.objects;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginMethod {
    private static List<PluginMethod> methods=JavaPlugin.getPlugin(PluginCoder.class).getMethods();
    private String name;
    private Map<String,List<String>> translatedMethodClasses=new HashMap<>();

    public PluginMethod(String name){
        this.name=name;methods.add(this);
    }
    public String getName() {
        return name;
    }

    public Map<String, List<String>> getTranslatedMethodClasses() {
        return translatedMethodClasses;
    }
    public static PluginMethod getMethod(String name){
        return methods.stream().filter(method->method.getName().equals(name)).findFirst().orElse(null);
    }
}
