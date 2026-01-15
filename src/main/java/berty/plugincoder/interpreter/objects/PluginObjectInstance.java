package berty.plugincoder.interpreter.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PluginObjectInstance {
    private PluginObject baseObject;
    private Map<String,Object> properties=new HashMap<>();

    protected PluginObjectInstance(PluginObject object){
        baseObject=object;
    }

    @Override
    public String toString(){
        String properties="";
        for(String property:this.properties.keySet().stream().sorted().collect(Collectors.toList())){
            properties+=property+"="+this.properties.get(property)+",";
        }
        if(properties.endsWith(","))properties=properties.substring(0,properties.length()-1);
        return baseObject.getName()+"["+properties+"]";
    }
    public PluginObject getBaseObject() {
        return baseObject;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
