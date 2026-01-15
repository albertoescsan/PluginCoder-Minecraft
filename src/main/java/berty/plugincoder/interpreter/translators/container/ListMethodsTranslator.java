package berty.plugincoder.interpreter.translators.container;

import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.*;

public class ListMethodsTranslator {

    public static void registerMethods(){
        List<String> listClass= new ArrayList<>(Arrays.asList(List.class.getTypeName()));
        listClass.add(List.class.getTypeName());
        List<String> listSetClass=new ArrayList<>(Arrays.asList(List.class.getTypeName(), Set.class.getTypeName()));
        PluginMethod get=new PluginMethod("get()");
        get.getTranslatedMethodClasses().put("get",listClass);
        PluginMethod set=new PluginMethod("set()");
        set.getTranslatedMethodClasses().put("set",listClass);
        PluginMethod add=new PluginMethod("add()");
        add.getTranslatedMethodClasses().put("add",listSetClass);
        PluginMethod clear=new PluginMethod("clear");
        clear.getTranslatedMethodClasses().put("clear",listSetClass);
        PluginMethod remove=new PluginMethod("remove()");
        remove.getTranslatedMethodClasses().put("remove",listSetClass);
        PluginMethod size=new PluginMethod("size");
        size.getTranslatedMethodClasses().put("size",listSetClass);
        PluginMethod contains=new PluginMethod("contains()");
        contains.getTranslatedMethodClasses().put("contains",listSetClass);
    }
}
