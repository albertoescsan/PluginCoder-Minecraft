package berty.plugincoder.GUI.guis.event;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsGUI {
    private PluginCoder mainPlugin;
    private List<Inventory> events=new ArrayList<>();

    private List<Inventory> eventsItemsGUI=new ArrayList<>();

    private int lastPageOpened=0;
    private Map<String,ItemStack> eventItems=new HashMap<>();
    private boolean deleteEvent=false;
    private int eventEditedIndex;
    public EventsGUI(PluginCoder plugin){
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> generateEventItems(), 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateGUI(), 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateEventItemsGUI(), 2);
    }
    private void generateEventItems(){
        ItemStack playerJoin= mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("WOOL"),1,(short)5):new ItemStack(Material.LIME_WOOL);
        ItemMeta meta=playerJoin.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"onPlayerJoin");
        playerJoin.setItemMeta(meta);
        eventItems.put("onPlayerJoin",playerJoin);
        ItemStack playerLeave= mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("WOOL"),1,(short)14):new ItemStack(Material.RED_WOOL);
        meta=playerLeave.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED+"onPlayerLeave");
        playerLeave.setItemMeta(meta);
        eventItems.put("onPlayerLeave",playerLeave);
        ItemStack playerClick=PluginCoder.getCoderGUI()
                .getPlayerHead("http://textures.minecraft.net/texture/532a5838fd9cd4c977f15071d6997ff5c7f956074a2da571a19ccefb03c57");
        meta=playerClick.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+"onPlayerClick");
        playerClick.setItemMeta(meta);
        eventItems.put("onPlayerClick",playerClick);
        ItemStack playerMove=new ItemStack(Material.COMPASS);
        meta=playerMove.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY+"onPlayerMove");
        playerMove.setItemMeta(meta);
        eventItems.put("onPlayerMove",playerMove);
        ItemStack playerTeleport=new ItemStack(Material.ENDER_PEARL);
        meta=playerTeleport.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+"onPlayerTeleport");
        playerTeleport.setItemMeta(meta);
        eventItems.put("onPlayerTeleport",playerTeleport);
        ItemStack blockPlace=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("GRASS_BLOCK"));
        meta=blockPlace.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN+"onBlockPlace");
        blockPlace.setItemMeta(meta);
        eventItems.put("onBlockPlace",blockPlace);
        ItemStack blockBreak=new ItemStack(Material.IRON_PICKAXE);
        meta=blockBreak.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+"onBlockBreak");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        blockBreak.setItemMeta(meta);
        eventItems.put("onBlockBreak",blockBreak);
        ItemStack inventoryClick=new ItemStack(Material.CHEST);
        meta=inventoryClick.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+"onInventoryClick");
        inventoryClick.setItemMeta(meta);
        eventItems.put("onInventoryClick",inventoryClick);
        ItemStack playerChat=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem("onPlayerChat");
        eventItems.put("onPlayerChat",playerChat);
        ItemStack playerDie=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("SKELETON_SKULL"),1,(short)3);
        meta=playerDie.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+"onPlayerDie");
        playerDie.setItemMeta(meta);
        eventItems.put("onPlayerDie",playerDie);
    }

    public void updateGUI() {
        events.clear();
        String eventsTitle= PluginCoder.getCoderGUI().getGuiText("eventsTitle").toUpperCase();
        ItemStack add=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("ENDER_EYE"));
        ItemMeta meta=add.getItemMeta();
        String addEventTitle=PluginCoder.getCoderGUI().getGuiText("addEventTitle");
        meta.setDisplayName(ChatColor.AQUA+addEventTitle);
        add.setItemMeta(meta);
        ItemStack delete=new ItemStack(deleteEvent?Material.LAVA_BUCKET:Material.BUCKET);
        meta=delete.getItemMeta();
        String deleteEventTitle=PluginCoder.getCoderGUI().getGuiText("deleteEventTitle");
        meta.setDisplayName(ChatColor.GRAY+deleteEventTitle);
        delete.setItemMeta(meta);
        if(mainPlugin.getSelectedPlugin().getListener().isEmpty()){
            createPage(0,add,delete,eventsTitle);return;
        }
        int i=10;
        int invIndex=0;
        for(String event: mainPlugin.getSelectedPlugin().getListener()){
            String eventName=event.replaceAll("^([^{]+)\\s*\\{(.*)}$","$1");
            ItemStack baseItem=eventItems.get(eventName).clone();
            ItemStack item=PluginCoder.getCoderGUI().addInstructionsToFunctionItem(event,baseItem);
            if(i==10)createPage(invIndex,add,delete,eventsTitle);
            events.get(invIndex).setItem(i,item);
            if((i+2)%9==0){
                if(i+2==events.get(invIndex).getSize()-9){
                    invIndex++;i=10;
                }else i+=3;
            }
            else i++;
        }

    }
    private void createPage(int invIndex,ItemStack add,ItemStack delete,String eventsTitle){
        events.add(Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',"&f&l"+eventsTitle.toUpperCase())));
        PluginCoder.getCoderGUI().createInventoryBase(events.get(invIndex),true);
        events.get(invIndex).setItem(26,add);
        events.get(invIndex).setItem(35,delete);
    }
    public void updateEventItemsGUI(){
        eventsItemsGUI.clear();
        int i=10;
        int invIndex=0;
        List<String> events=new ArrayList<>();
        events.add("onPlayerJoin");events.add("onPlayerLeave");events.add("onPlayerClick");events.add("onPlayerMove");events.add("onPlayerTeleport");
        events.add("onPlayerDie");events.add("onPlayerChat");events.add("onBlockPlace");events.add("onBlockBreak");events.add("onInventoryClick");
        //TODO añadir más eventos
        String eventsSelectTitle= PluginCoder.getCoderGUI().getGuiText("eventsSelectTitle").toUpperCase();
        eventsItemsGUI.add(Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',"&f&l"+eventsSelectTitle)));
        PluginCoder.getCoderGUI().createInventoryBase(eventsItemsGUI.get(0),true);
        for(String event:events){
            ItemStack item=this.getEventItem(event);
            eventsItemsGUI.get(invIndex).setItem(i,item);
            if((i+2)%9==0){
                if(i+2==eventsItemsGUI.get(invIndex).getSize()-9){
                    invIndex++;
                    eventsItemsGUI.add(Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',"&f&l"+eventsSelectTitle)));
                    PluginCoder.getCoderGUI().createInventoryBase(eventsItemsGUI.get(invIndex),true);
                }else i+=3;
            }
            else i++;
        }
    }
    private ItemStack getEventItem(String event){
        ItemStack eventItem=eventItems.get(event).clone();
        String description=PluginCoder.getCoderGUI().getGuiText(event);
        return PluginCoder.getCoderGUI().createItemWithDescription(eventItem,description);
    }

    public void updateLastEventEdited() {
        Inventory inv=events.get(eventEditedIndex/28);
        String function= mainPlugin.getSelectedPlugin().getListener().get(eventEditedIndex);
        ItemStack newItem= PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(function);
        int eventItemIndex=eventEditedIndex%28;
        eventItemIndex=eventItemIndex+(10+2*(eventItemIndex/7));
        ItemStack oldItem=inv.getItem(eventItemIndex).clone();
        ItemMeta meta=oldItem.getItemMeta();
        if(newItem.getItemMeta().getLore()==null)meta.setLore(new ArrayList<>());
        else meta.setLore(newItem.getItemMeta().getLore());
        oldItem.setItemMeta(meta);
        inv.setItem(eventItemIndex,oldItem);
    }
    public List<Inventory> getGUI() {
        return events;
    }


    public List<Inventory> getEventsItemsGUI() {
        return eventsItemsGUI;
    }

    public boolean isDeleteEvent() {
        return deleteEvent;
    }

    public void setDeleteEvent(boolean deleteEvent) {
        this.deleteEvent = deleteEvent;
    }

    public int getLastPageOpened() {
        return lastPageOpened;
    }

    public void setLastPageOpened(int lastPageOpened) {
        this.lastPageOpened = lastPageOpened;
    }

    public void setEventEditedIndex(Inventory inventory, int slot) {
        eventEditedIndex=events.indexOf(inventory)*28+slot-(10+2*(slot/9-1));
    }

    public int getEventEditedIndex() {
        return eventEditedIndex;
    }
}
