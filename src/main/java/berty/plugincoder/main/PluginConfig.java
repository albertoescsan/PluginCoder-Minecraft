package berty.plugincoder.main;

import org.bukkit.plugin.java.JavaPlugin;
import berty.plugincoder.interpreter.objects.PluginObjectInstance;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.writer.PluginFileWriter;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginConfig {

    public static void load(Plugin plugin){
        String configPath = JavaPlugin.getPlugin(PluginCoder.class).getDataFolder().getParentFile().getPath() +
                "/PluginCoder/plugins/"+ plugin.getName()+"/config.yml";
        File configFile=new File(configPath);
        if(!configFile.exists())return;

    }
    public static void save(Plugin plugin){
        String pluginFolderPath = JavaPlugin.getPlugin(PluginCoder.class).getDataFolder().getParentFile().getPath() +
                "/PluginCoder/plugins/"+ plugin.getName();
        File pluginFolder=new File(pluginFolderPath);
        if(!pluginFolder.exists())pluginFolder.mkdir();
        File configFile=new File(pluginFolderPath+"/config.yml");
        try{
            if(!configFile.exists())configFile.createNewFile();
        }catch (Exception e){return;}
        String configContent="";
        for(String property:plugin.getMainObjectInstance().getProperties().keySet()){
            Object value=plugin.getMainObjectInstance().getProperties().get(property);
            configContent+=property+": "+getStringValue(value,"")+"\n";
        }
        PluginFileWriter.writeInFile(configFile,configContent);
    }
    private static String getStringValue(Object value,String indentationText){
        try{
            if (value == null) return "null";
            indentationText+="  ";
            String stringValue="";
            Class clazz=value.getClass();
            if(clazz.isPrimitive()||Number.class.isAssignableFrom(clazz)||clazz.getTypeName().equals(Boolean.class.getTypeName())||clazz.isEnum())return value.toString();
            if(clazz.getTypeName().equals(String.class.getTypeName()))return "\""+value+"\"";
            if(value instanceof PluginObjectInstance){
                PluginObjectInstance instance= (PluginObjectInstance) value;
                for(String property:instance.getProperties().keySet()){
                    stringValue+=indentationText+property+": "+getStringValue(instance.getProperties().get(property),indentationText+1)+"\n";
                }
            }
            else if(clazz.isAssignableFrom(ArrayList.class)){
                stringValue+=indentationText+"[\n";
                for(Object listContent:(List<Object>) value){
                    stringValue+=indentationText+getStringValue(listContent,indentationText)+",\n";
                }
                stringValue=(!stringValue.endsWith(",\n")?stringValue:stringValue.substring(0,stringValue.length()-2))+"\n]";
            }else if(clazz.isAssignableFrom(HashMap.class)){
                stringValue+=indentationText+"{\n";
                Map<Object,Object> map=(Map<Object,Object>) value;
                for(Object mapContent:map.keySet()){
                    stringValue+=indentationText+getStringValue(mapContent,indentationText)+": "+getStringValue(map.get(mapContent),indentationText)+",\n";
                }
                stringValue=(!stringValue.endsWith(",\n")?stringValue:stringValue.substring(0,stringValue.length()-2))+"\n}";
            }else{
                for (Field field : clazz.getDeclaredFields()) {
                    if(Modifier.isStatic(field.getModifiers()))continue;
                    Object fieldValue=getFieldValue(value,field);
                    if(void.class.equals(fieldValue)){
                        Method getMethod=getPropertyGetMethod(clazz,field.getName());
                        if(getMethod==null)continue;
                        fieldValue=getMethod.invoke(value);
                    }
                    stringValue+=indentationText+field.getName()+": "+getStringValue(fieldValue,indentationText+1)+"\n";
                }
            }
            return stringValue;
        }catch (Exception e){e.printStackTrace();return "";}
    }
    private static Object getFieldValue(Object classValue,Field field){
        try{
            field.setAccessible(true);
            return field.get(classValue);
        }catch (Exception e){return void.class;}
    }
    private static Method getPropertyGetMethod(Class clazz,String propertyName){
        try{
            return clazz.getMethod("get"+String.valueOf(propertyName.charAt(0)).toUpperCase()+propertyName.substring(1,propertyName.length()));
        }catch (Exception e){return null;}
    }
}

