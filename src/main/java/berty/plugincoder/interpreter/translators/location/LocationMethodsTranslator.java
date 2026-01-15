package berty.plugincoder.interpreter.translators.location;

import org.bukkit.Location;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.Arrays;
import java.util.List;

public class LocationMethodsTranslator {
    public static void registerMethods(){
        List<String> locationClass= Arrays.asList(Location.class.getTypeName());
        PluginMethod x=new PluginMethod("x");
        x.getTranslatedMethodClasses().put("getX",locationClass);
        PluginMethod y=new PluginMethod("y");
        y.getTranslatedMethodClasses().put("getY",locationClass);
        PluginMethod z=new PluginMethod("z");
        z.getTranslatedMethodClasses().put("getZ",locationClass);
        PluginMethod yaw=new PluginMethod("yaw");
        yaw.getTranslatedMethodClasses().put("getYaw",locationClass);
        PluginMethod pitch=new PluginMethod("pitch");
        pitch.getTranslatedMethodClasses().put("getPitch",locationClass);
    }
}
