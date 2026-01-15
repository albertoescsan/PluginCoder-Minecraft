package berty.plugincoder.interpreter.translators.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryMethodsTranslator {

    public static void registerMethods(){
        List<String> inventoryClasses= Arrays.asList(Inventory.class.getTypeName(), PlayerInventory.class.getTypeName());
        PluginMethod get=PluginMethod.getMethod("get()");
        get.getTranslatedMethodClasses().put("getItem",inventoryClasses);
        PluginMethod set=PluginMethod.getMethod("set()");
        set.getTranslatedMethodClasses().put("setItem",inventoryClasses);
        PluginMethod add=PluginMethod.getMethod("add()");
        add.getTranslatedMethodClasses().put("addItem",inventoryClasses);
        PluginMethod contents=new PluginMethod("items");
        contents.getTranslatedMethodClasses().put("getContents",new ArrayList<>(inventoryClasses));
        PluginMethod contentsP=new PluginMethod("items()");
        contentsP.getTranslatedMethodClasses().put("setContents",inventoryClasses);
        List<String> playerInventoryClass=Arrays.asList(PlayerInventory.class.getTypeName());
        PluginMethod armor=new PluginMethod("armor");
        armor.getTranslatedMethodClasses().put("getArmorContents",playerInventoryClass);
        PluginMethod armorP=new PluginMethod("armor()");
        armorP.getTranslatedMethodClasses().put("setArmorContents",playerInventoryClass);
        PluginMethod helmet=new PluginMethod("helmet");
        helmet.getTranslatedMethodClasses().put("getHelmet",new ArrayList<>(playerInventoryClass));
        PluginMethod helmetP=new PluginMethod("helmet()");
        helmetP.getTranslatedMethodClasses().put("setHelmet",new ArrayList<>(playerInventoryClass));
        PluginMethod chestplate=new PluginMethod("chestplate");
        chestplate.getTranslatedMethodClasses().put("getChestplate",new ArrayList<>(playerInventoryClass));
        PluginMethod chestplateP=new PluginMethod("chestplate()");
        chestplateP.getTranslatedMethodClasses().put("setChestplate",new ArrayList<>(playerInventoryClass));
        PluginMethod leggins=new PluginMethod("leggins");
        leggins.getTranslatedMethodClasses().put("getLeggins",new ArrayList<>(playerInventoryClass));
        PluginMethod legginsP=new PluginMethod("leggins()");
        legginsP.getTranslatedMethodClasses().put("setLeggins",new ArrayList<>(playerInventoryClass));
        PluginMethod boots=new PluginMethod("boots");
        boots.getTranslatedMethodClasses().put("getBoots",new ArrayList<>(playerInventoryClass));
        PluginMethod bootsP=new PluginMethod("boots()");
        bootsP.getTranslatedMethodClasses().put("setBoots",new ArrayList<>(playerInventoryClass));
    }
}
