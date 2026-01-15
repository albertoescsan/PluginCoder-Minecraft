package berty.plugincoder.GUI.guis.parameters;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ContentParams {
    private String name;
    private List<String> paramTypes;

    public ContentParams(String name,List<String> paramTypes){
        PluginCoder pluginCoder= JavaPlugin.getPlugin(PluginCoder.class);
        for(int i=0;i<paramTypes.size();i++){
            paramTypes.set(i,pluginCoder.getCodeUtils().checkCustomType(paramTypes.get(i)));
        };
        this.name=name;this.paramTypes=paramTypes;
    }

    public String getName() {
        return name;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }
}
