package berty.plugincoder.GUI.guis.plugins;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PluginsGUI {

    private PluginCoder mainPlugin;
    private boolean isEditingPluginName=false;
    public PluginsGUI(PluginCoder plugin){
        this.mainPlugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateGUI(), 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createPluginEditor(), 2);
    }
    private List<Inventory> pluginsGUI=new ArrayList<>();

    private Inventory pluginEditor;
    private int pluginSelectedIndex;
    private boolean deletePlugin=false;

    private int lastOpenedPage=0;
    public void updateGUI(){
        pluginsGUI.clear();
        pluginSelectedIndex=-1;
        ItemStack add=new ItemStack(Material.BOOK);
        ItemMeta meta=add.getItemMeta();
        String addTitle = PluginCoder.getCoderGUI().getGuiText("addPlugin");
        meta.setDisplayName(ChatColor.GOLD+addTitle);
        add.setItemMeta(meta);
        ItemStack delete=new ItemStack(deletePlugin?Material.LAVA_BUCKET:Material.BUCKET);
        meta=delete.getItemMeta();
        String deleteTitle = PluginCoder.getCoderGUI().getGuiText("deletePlugin");
        meta.setDisplayName(ChatColor.GRAY+deleteTitle);
        delete.setItemMeta(meta);
        if(mainPlugin.getPlugins().isEmpty()){
            createPage(0,add,delete);return;
        }
        int i=10;
        int invIndex=0;
        for(Plugin plugin:mainPlugin.getPlugins()){
            ItemStack pluginItem=new ItemStack(Material.BOOK);
            meta=pluginItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+plugin.getName());
            if(plugin.getName().equals(mainPlugin.getSelectedPlugin().getName())){
                meta.addEnchant(Enchantment.values()[0],1,false);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            List<String> description=new ArrayList<>();
            description.add(plugin.isEnabled()?ChatColor.GREEN+PluginCoder.getCoderGUI().getGuiText("enabled"):
                    ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("disabled"));
            meta.setLore(description);
            pluginItem.setItemMeta(meta);
            if(i==10)createPage(invIndex,add,delete);
            pluginsGUI.get(invIndex).setItem(i,pluginItem);
            if((i+2)%9==0){
                if(i+2==pluginsGUI.get(invIndex).getSize()-9){
                    invIndex++;i=10;
                }else i+=3;
            }
            else i++;
        }
    }
    public void createPluginEditor(){
        String pluginEditorName=PluginCoder.getCoderGUI().getGuiText("pluginEditorTitle");
        pluginEditor=Bukkit.createInventory(null,45,ChatColor.translateAlternateColorCodes('&',"&f&l"+pluginEditorName).toUpperCase());
        PluginCoder.getCoderGUI().createCenteredItemInventory(pluginEditor);
        ItemStack rename=new ItemStack(Material.NAME_TAG);
        ItemMeta meta=rename.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("renamePlugin"));
        rename.setItemMeta(meta);
        pluginEditor.setItem(38,rename);
    }
    public void updatePluginEditor(Plugin plugin){
        ItemStack enable=getActivationItem(plugin);
        ItemStack pluginItem=new ItemStack(Material.BOOK);
        ItemMeta meta=pluginItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+plugin.getName());
        if(!plugin.getName().equals(mainPlugin.getSelectedPlugin().getName())){
            List<String> selectPlugin=new ArrayList<>();
            selectPlugin.add(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("selectPlugin"));
            meta.setLore(selectPlugin);
        }
        pluginItem.setItemMeta(meta);
        pluginEditor.setItem(42,enable);pluginEditor.setItem(22,pluginItem);
    }
    public ItemStack getActivationItem(Plugin plugin){
        ItemStack enable=plugin.isEnabled()?(mainPlugin.getVersionNumber()<13?new ItemStack(Material.getMaterial("WOOL"),1,(short)5):
                new ItemStack(Material.LIME_WOOL)):(mainPlugin.getVersionNumber()<13?new ItemStack(Material.getMaterial("WOOL"),1,(short)14):
                new ItemStack(Material.RED_WOOL));
        ItemMeta meta= enable.getItemMeta();
        meta.setDisplayName(plugin.isEnabled()?ChatColor.GREEN+PluginCoder.getCoderGUI().getGuiText("pluginEnabled")
                :ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("pluginDisabled"));
        enable.setItemMeta(meta);
        return enable;
    }
    private void createPage(int invIndex, ItemStack add, ItemStack delete){
        pluginsGUI.add(Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',"&f&lPLUGINS")));
        PluginCoder.getCoderGUI().createInventoryBase(pluginsGUI.get(invIndex),true);
        pluginsGUI.get(invIndex).setItem(26,add);
        pluginsGUI.get(invIndex).setItem(35,delete);
    }
    public void setPlayerToEditPluginName(Player p){
        isEditingPluginName=true;
        p.closeInventory();
        if(pluginSelectedIndex==-1)p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writePluginName"));
        else p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeNewPluginName"));
    }
    public boolean checkPluginName(String name, Plugin plugin) {
        if(plugin!=null&&name.equalsIgnoreCase(plugin.getName()))return false;
        if(!mainPlugin.getPlugins().stream().anyMatch(pl->pl.getName().equalsIgnoreCase(name)))return true;
        else ErrorManager.existingPluginWithThisName(name);
        return false;
    }
    public List<Inventory> getGUI() {
        return pluginsGUI;
    }

    public boolean isDeletePlugin() {
        return deletePlugin;
    }

    public void setDeletePlugin(boolean deletePlugin) {
        this.deletePlugin = deletePlugin;
    }

    public Inventory getPluginEditor() {
        return pluginEditor;
    }

    public int getPluginSelectedIndex() {
        return pluginSelectedIndex;
    }

    public void setPluginSelectedIndex(int pluginSelectedIndex) {
        this.pluginSelectedIndex = pluginSelectedIndex;
    }

    public boolean isEditingPluginName() {
        return isEditingPluginName;
    }

    public void setEditingPluginName(boolean editingPluginName) {
        isEditingPluginName = editingPluginName;
    }

    public int getLastOpenedPage() {
        return lastOpenedPage;
    }

    public void setLastOpenedPage(int lastOpenedPage) {
        this.lastOpenedPage = lastOpenedPage;
    }
}
