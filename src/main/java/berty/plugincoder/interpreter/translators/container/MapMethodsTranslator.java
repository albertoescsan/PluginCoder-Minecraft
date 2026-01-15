package berty.plugincoder.interpreter.translators.container;

import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapMethodsTranslator {
    public static void registerMethods(){
        List<String> mapClass= Arrays.asList(Map.class.getTypeName());
        PluginMethod.getMethod("clear").getTranslatedMethodClasses().get("clear").add(Map.class.getTypeName());
        PluginMethod put=new PluginMethod("put()");
        put.getTranslatedMethodClasses().put("put",mapClass);
        PluginMethod keys=new PluginMethod("keys");
        keys.getTranslatedMethodClasses().put("keySet",mapClass);
        PluginMethod values=new PluginMethod("values");
        values.getTranslatedMethodClasses().put("values",mapClass);
        PluginMethod get=PluginMethod.getMethod("get()");
        get.getTranslatedMethodClasses().get("get").addAll(mapClass);
    }
}
