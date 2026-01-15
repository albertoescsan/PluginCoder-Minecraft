package berty.plugincoder.interpreter.translators.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.Arrays;
import java.util.List;

public class ItemMethodsTranslator {
    public static void registerMethods(){
        List<String> itemStackClass= Arrays.asList(ItemStack.class.getTypeName());
        List<String> itemMetaClass=Arrays.asList(ItemMeta.class.getTypeName());
        PluginMethod meta=new PluginMethod("meta");
        meta.getTranslatedMethodClasses().put("getItemMeta",itemStackClass);
        PluginMethod metaP=new PluginMethod("meta()");
        metaP.getTranslatedMethodClasses().put("setItemMeta",itemStackClass);
        PluginMethod.getMethod("name").getTranslatedMethodClasses().put("getDisplayName",itemMetaClass);
        PluginMethod.getMethod("name()").getTranslatedMethodClasses().get("setDisplayName").add(ItemMeta.class.getTypeName());
        PluginMethod lore=new PluginMethod("lore");
        lore.getTranslatedMethodClasses().put("getLore",itemMetaClass);
        PluginMethod loreP=new PluginMethod("lore()");
        loreP.getTranslatedMethodClasses().put("setLore",itemMetaClass);
    }
}
