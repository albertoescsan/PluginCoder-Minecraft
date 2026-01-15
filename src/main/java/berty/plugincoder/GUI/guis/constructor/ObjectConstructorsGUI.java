package berty.plugincoder.GUI.guis.constructor;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectConstructorsGUI {

    private PluginCoder plugin;
    private List<Inventory> gui=new ArrayList<>();
    public ObjectConstructorsGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateGUI(), 2);
    }

    public void updateGUI() {
        gui.clear();
        List<String> objects=new ArrayList<>();
        for(String object:plugin.getSelectedPlugin().getObjects().stream().map(obj->obj.getName()).sorted().collect(Collectors.toList())){
            objects.add(object);
            if(objects.size()==7){
                createPage(objects);
                objects.clear();
            }
        }
        if(objects.size()>0||gui.size()==0)createPage(objects);
    }

    private void createPage(List<String>objects){
        Inventory inv=Bukkit.createInventory(null,27," ");
        PluginCoder.getCoderGUI().createInventoryBase(inv,true);
        for(int i=0;i<7;i++){
            if(objects.size()==i)break;
            ItemStack object=new ItemStack(Material.CHEST);
            ItemMeta meta= object.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+objects.get(i));
            object.setItemMeta(meta);
            inv.setItem(10+i,object);
        }
        gui.add(inv);
    }

    public List<Inventory> getGUI() {
        return gui;
    }
}
