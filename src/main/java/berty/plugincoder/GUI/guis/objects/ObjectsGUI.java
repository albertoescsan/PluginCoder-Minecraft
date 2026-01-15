package berty.plugincoder.GUI.guis.objects;

import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ObjectsGUI {

    private PluginCoder plugin;

    private List<Inventory> objectsGUI=new ArrayList<>();
    private boolean deleteObject=false;
    private boolean creatingObject=false;
    private int lastPageOpened=0;
    public ObjectsGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateGUI(), 2);
    }
    public void updateGUI() {
        objectsGUI.clear();
        String objectsTitle=PluginCoder.getCoderGUI().getGuiText("objectsTitle").toUpperCase();
        ItemStack add=new ItemStack(Material.CHEST);
        ItemMeta meta=add.getItemMeta();
        String addTitle = PluginCoder.getCoderGUI().getGuiText("addObject");
        meta.setDisplayName(ChatColor.GOLD+addTitle);
        add.setItemMeta(meta);
        ItemStack delete=new ItemStack(deleteObject?Material.LAVA_BUCKET:Material.BUCKET);
        meta=delete.getItemMeta();
        String deleteTitle = PluginCoder.getCoderGUI().getGuiText("deleteObject");
        meta.setDisplayName(ChatColor.GRAY+deleteTitle);
        delete.setItemMeta(meta);
        if(plugin.getSelectedPlugin().getObjects().isEmpty()){
            createPage(0,add,delete,objectsTitle);return;
        }
        int i=10;
        int invIndex=0;
        for(PluginObject object:plugin.getSelectedPlugin().getObjects()){
            ItemStack objectItem=getObjectItem(object);
            if(i==10)createPage(invIndex,add,delete,objectsTitle);
            objectsGUI.get(invIndex).setItem(i,objectItem);
            if((i+2)%9==0){
                if(i+2==objectsGUI.get(invIndex).getSize()-9){
                    invIndex++;i=10;
                }else i+=3;
            }
            else i++;
        }

    }
    private ItemStack getObjectItem(PluginObject object) {
        ItemStack objectItem=new ItemStack(Material.CHEST);
        ItemMeta meta=objectItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+object.getName());
        objectItem.setItemMeta(meta);
        return objectItem;
    }
    private void createPage(int invIndex,ItemStack add,ItemStack delete,String objectsTitle){
        objectsGUI.add(Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',"&f&l"+objectsTitle.toUpperCase())));
        PluginCoder.getCoderGUI().createInventoryBase(objectsGUI.get(invIndex),true);
        objectsGUI.get(invIndex).setItem(26,add);
        objectsGUI.get(invIndex).setItem(35,delete);
    }
    public void addObject(PluginObject object) {
        ItemStack objectItem=getObjectItem(object);
        if(objectsGUI.get(objectsGUI.size()-1).getItem(43)!=null){
            String objectsTitle=PluginCoder.getCoderGUI().getGuiText("objectsTitle").toUpperCase();
            ItemStack add=new ItemStack(Material.CHEST);
            ItemMeta meta=add.getItemMeta();
            String addTitle = PluginCoder.getCoderGUI().getGuiText("addObject");
            meta.setDisplayName(ChatColor.GOLD+addTitle);
            add.setItemMeta(meta);
            ItemStack delete=new ItemStack(deleteObject?Material.LAVA_BUCKET:Material.BUCKET);
            meta=delete.getItemMeta();
            String deleteTitle = PluginCoder.getCoderGUI().getGuiText("deleteObject");
            meta.setDisplayName(ChatColor.GRAY+deleteTitle);
            delete.setItemMeta(meta);
            createPage(0,add,delete,objectsTitle);
        }
        objectsGUI.get(objectsGUI.size()-1).addItem(objectItem);
    }
    public void setToWriteObjectName(Player p){
        creatingObject=true;
        p.closeInventory();
        p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeObjectName"));
    }
    public List<Inventory> getGUI() {
        return objectsGUI;
    }

    public boolean isDeleteObject() {
        return deleteObject;
    }

    public void setDeleteObject(boolean deleteObject) {
        this.deleteObject = deleteObject;
    }

    public boolean isCreatingObject() {
        return creatingObject;
    }

    public void setCreatingObject(boolean creatingObject) {
        this.creatingObject = creatingObject;
    }

    public int getLastPageOpened() {
        return lastPageOpened;
    }

    public void setLastPageOpened(int lastPageOpened) {
        this.lastPageOpened = lastPageOpened;
    }
}
