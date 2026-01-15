package berty.plugincoder.interpreter.objects;

import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PluginObject {

    private Plugin plugin;
    private String name;
    private Map<String,String> propertiesEqualities =new HashMap<>();
    private PluginObject parent;

    private List<String> functions=new ArrayList<>();

    private List<String> constructors=new ArrayList<>();

    public PluginObject(Plugin plugin,String name){this.plugin=plugin;this.name=name;}

    public PluginObjectInstance getInstance(){
        PluginObjectInstance newObject=new PluginObjectInstance(this);
        try{
            //copiar las propiedades del objeto para asegurarse que son propiedades independientes
            Map<String,Object> variables=JavaPlugin.getPlugin(PluginCoder.class).getPluginVars(plugin);
            if(name.equals("Plugin")) variables.put("plugin",new PluginObjectInstance(this));
            Map<String,Object> properties=new HashMap<>();
            Map<String,String> propertiesEqualities=getPropertyEqualities();
            for(String property:propertiesEqualities.keySet()){
                String equal=propertiesEqualities.get(property);
                if(equal==null){properties.put(property,null);continue;}
                properties.put(property,JavaPlugin.getPlugin(PluginCoder.class).getCodeExecuter().executeInstruction(equal,"",variables));
            }
            newObject.getProperties().putAll(properties);
        }catch (Exception e){
            e.printStackTrace();
        }
        return newObject;
    }
    public String getName() {
        return name;
    }
    public PluginObject getParent() {
        return parent;
    }
    public void setParent(PluginObject parent) {
        this.parent=parent;
    }
    public List<String> getDeclaredProperties() {
        return new ArrayList<>(propertiesEqualities.keySet());
    }
    public List<String> getDeclaredFunctions() {
        return functions;
    }
    public List<String> getConstructors() {
        return constructors;
    }
    public List<String> getProperties() {
        List<String> properties=new ArrayList<>(this.propertiesEqualities.keySet());
        if(parent==null)return properties;
        properties.addAll(parent.getProperties());
        return properties;
    }
    public void addProperty(String property,String value){
        propertiesEqualities.put(property,value);
    }
    public void removeProperty(String property){
        propertiesEqualities.remove(property);
    }

    public List<String> getFunctions() {
        List<String> objectFunctions=new ArrayList<>(functions);
        if(parent==null)return objectFunctions;
        Set<String> functionParams=new HashSet<>();
        objectFunctions.stream().forEach(function->{
            String functionContent=function.replaceAll("^([^{;]+)\\{(.+)$","$1");
            String functionName=functionContent.replaceAll("^([^(]+)\\((.*)$","$1");
            int params=JavaPlugin.getPlugin(PluginCoder.class).getCodeExecuter().getStringParameters(functionContent).size();
            functionParams.add(functionName+"->"+params);
        });
        parent.getFunctions().stream().forEach(function->{
            String functionContent=function.replaceAll("^([^{;]+)\\{(.+)$","$1");
            String functionName=functionContent.replaceAll("^([^(]+)\\((.*)$","$1");
            int params=JavaPlugin.getPlugin(PluginCoder.class).getCodeExecuter().getStringParameters(functionContent).size();
            if(functionParams.contains(functionName+"->"+params))return;
            objectFunctions.add(function);
        });
        return objectFunctions;
    }

    public Map<String, String> getPropertyEqualities() {
        Map<String, String> propertiesEqualities=new HashMap<>(this.propertiesEqualities);
        if(parent==null)return propertiesEqualities;
        Map<String,String> parentPropertiesEqualities=parent.getPropertyEqualities();
        parentPropertiesEqualities.keySet().stream().forEach(property->{
            if(!propertiesEqualities.containsKey(property))
                propertiesEqualities.put(property,parentPropertiesEqualities.get(property));
        });
        return propertiesEqualities;
    }
    public Map<String, String> getDeclaredPropertyEqualities(){
        return propertiesEqualities;
    }
    public Plugin getPlugin() {
        return plugin;
    }
}
