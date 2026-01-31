package berty.plugincoder.GUI.guis.text;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class TextHexColorGUI {

    private PluginCoder mainPlugin;
    private Inventory gui;

    public TextHexColorGUI(PluginCoder plugin) {
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private void createInventory() {
        gui=Bukkit.createInventory(null,45," ");
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        updateInventoryLanguage();
    }

    private void updateInventoryLanguage() {
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
    }
}
