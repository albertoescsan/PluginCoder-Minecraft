package berty.plugincoder.GUI.guis.math;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NumberGUI {
    private PluginCoder plugin;
    private Inventory gui;

    public NumberGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private void createInventory(){
        gui= Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        ItemStack uno= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/ca516fbae16058f251aef9a68d3078549f48f6d5b683f19cf5a1745217d72cc");
        ItemMeta meta=uno.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"1");
        uno.setItemMeta(meta);
        ItemStack dos= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/4698add39cf9e4ea92d42fadefdec3be8a7dafa11fb359de752e9f54aecedc9a");
        meta=dos.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"2");
        dos.setItemMeta(meta);
        ItemStack tres= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/fd9e4cd5e1b9f3c8d6ca5a1bf45d86edd1d51e535dbf855fe9d2f5d4cffcd2");
        meta=tres.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"3");
        tres.setItemMeta(meta);
        ItemStack cuatro= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/f2a3d53898141c58d5acbcfc87469a87d48c5c1fc82fb4e72f7015a3648058");
        meta=cuatro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"4");
        cuatro.setItemMeta(meta);
        ItemStack cinco= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/d1fe36c4104247c87ebfd358ae6ca7809b61affd6245fa984069275d1cba763");
        meta=cinco.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"5");
        cinco.setItemMeta(meta);
        ItemStack seis= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/3ab4da2358b7b0e8980d03bdb64399efb4418763aaf89afb0434535637f0a1");
        meta=seis.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"6");
        seis.setItemMeta(meta);
        ItemStack siete= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/297712ba32496c9e82b20cc7d16e168b035b6f89f3df014324e4d7c365db3fb");
        meta=siete.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"7");
        siete.setItemMeta(meta);
        ItemStack ocho= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/abc0fda9fa1d9847a3b146454ad6737ad1be48bdaa94324426eca0918512d");
        meta=ocho.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"8");
        ocho.setItemMeta(meta);
        ItemStack nueve= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/d6abc61dcaefbd52d9689c0697c24c7ec4bc1afb56b8b3755e6154b24a5d8ba");
        meta=nueve.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"9");
        nueve.setItemMeta(meta);
        ItemStack cero= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/3f09018f46f349e553446946a38649fcfcf9fdfd62916aec33ebca96bb21b5");
        meta=cero.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"0");
        cero.setItemMeta(meta);
        ItemStack point= PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/323e619dcb7511cdc252a5dca8565b19d952ac9f82d467e66c52242f9cd88fa");
        meta=point.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+".");
        point.setItemMeta(meta);
        gui.setItem(12,uno);gui.setItem(13,dos);gui.setItem(14,tres);
        gui.setItem(21,cuatro);gui.setItem(22,cinco);gui.setItem(23,seis);
        gui.setItem(30,siete);gui.setItem(31,ocho);gui.setItem(32,nueve);
        gui.setItem(40,cero);gui.setItem(49,point);
        ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemStack gris=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)8):new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for(int i=0;i<4;i++){
            gui.setItem(9*(i+1)+1,negro);gui.setItem(9*(i+2)-2,negro);
            gui.setItem(9*(i+1)+2,gris);gui.setItem(9*(i+2)-3,gris);
        }
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        ItemStack red=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("WOOL"),1,(short)14):new ItemStack(Material.RED_WOOL);
        ItemMeta meta=red.getItemMeta();
        meta.setDisplayName(ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("deleteLastSymbol"));
        red.setItemMeta(meta);
        ItemStack green=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("WOOL"),1,(short)5):new ItemStack(Material.LIME_WOOL);
        meta=green.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+PluginCoder.getCoderGUI().getGuiText("save"));
        green.setItemMeta(meta);
        gui.setItem(39,red);gui.setItem(41,green);
    }
    public void updateGUI(String number){
        ItemStack numberItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        ItemMeta meta=numberItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+number);
        numberItem.setItemMeta(meta);
        gui.setItem(4,numberItem);
    }
    public boolean saveChanges(boolean closingInventoryWithoutReturning) {
        String newNumber=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        return PluginCoder.getCoderGUI().getMathGUI().saveToMathGUI(newNumber,closingInventoryWithoutReturning);
    }

    public Inventory getGUI() {
        return gui;
    }

    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
}
