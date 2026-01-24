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

public class ListConstructorGUI {

    private PluginCoder plugin;
    private Inventory gui;
    private List<String> elements=new ArrayList<>();
    private List<String> previousLists=new ArrayList<>();
    private List<Integer> elementSlot=new ArrayList<>();
    private boolean deletingElement;
    private List<Boolean> newElement=new ArrayList<>();
    private List<Integer> listBeginIndex=new ArrayList<>();
    public ListConstructorGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    private void createInventory() {
        gui=Bukkit.createInventory(null,27," ");
        PluginCoder.getCoderGUI().createInventoryBase(gui,true);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        gui.setItem(18,PluginCoder.getCoderGUI().getBackItem());
        gui.setItem(26,PluginCoder.getCoderGUI().getNextItem());
        ItemStack delete=new ItemStack(deletingElement?Material.LAVA_BUCKET:Material.BUCKET);
        ItemMeta meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteElement"));
        delete.setItemMeta(meta);
        gui.setItem(24,delete);
        ItemStack add=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.MAP));
        meta= add.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addElement"));
        add.setItemMeta(meta);
        gui.setItem(20,add);
    }

    private void updateGUIWithPreviousInstruction(){
        updateGUIContent(previousLists.get(previousLists.size()-1));
    }
    public void updateGUI(String instruction) {
        deletingElement=false;
        listBeginIndex.add(0);
        previousLists.add(instruction);
        updateGUIContent(instruction);
    }
    public void updateGUIContent(String instruction) {
        elements.clear();
        if(!(instruction.startsWith("[")&&instruction.endsWith("]")))instruction="[]";
        elements.addAll(plugin.getCodeExecuter().getStringParameters("("+instruction.substring(1,instruction.length()-1)+")"));
        renderElements();
        updateListSign();
    }

    public ItemStack getListElementItem(String element){
        ItemStack elementItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta= elementItem.getItemMeta();
        meta.setDisplayName(PluginCoder.getCoderGUI().putTextColor(element));
        elementItem.setItemMeta(meta);
        return elementItem;
    }
    public void saveChanges(boolean closingInventoryWithoutReturning){
        String list=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        previousLists.remove(previousLists.size()-1);
        PluginCoder.getCoderGUI().getConstructorsGUI().saveToConstructorsGUI(list,closingInventoryWithoutReturning);
    }
    public void saveToListConstrutorGUI(String instruction,boolean closingInventoryWithoutReturning){
        updateGUIWithPreviousInstruction();
        if(newElement.remove(newElement.size()-1)){
            elements.add(instruction);
            renderElements();
        }else{
            int invSlot=this.elementSlot.remove(this.elementSlot.size()-1);
            gui.setItem(invSlot,getListElementItem(instruction));
            elements.set(listBeginIndex.get(listBeginIndex.size()-1)+invSlot-10,instruction);
        }
        updateListSign();
        String list=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        previousLists.set(previousLists.size()-1,list);
        if(closingInventoryWithoutReturning)saveChanges(true);

    }
    private void renderElements() {
        for(int i=0;i<7;i++)gui.setItem(10+i,null);
        int begin=listBeginIndex.get(listBeginIndex.size()-1);
        for(int i=0;i<7;i++){
            if(elements.size()==i+begin)return;
            gui.setItem(10+i,getListElementItem(elements.get(i+begin)));
        }
    }
    private void updateListSign(){
       String listInstruction="[";
        for(String element:elements){
            listInstruction+=element+",";
        }
       if(listInstruction.endsWith(","))listInstruction=listInstruction.substring(0,listInstruction.length()-1);
       listInstruction+="]";
       ItemStack sign=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
       ItemMeta meta=sign.getItemMeta();
       meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(listInstruction));
       sign.setItemMeta(meta);
       gui.setItem(4,sign);

    }

    public void removeElement(int invSlot) {
        int index=invSlot-10+listBeginIndex.get(listBeginIndex.size()-1);
        elements.remove(index);
        renderElements();
        updateListSign();
        String list=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        previousLists.set(previousLists.size()-1,list);
    }
    public Inventory getGUI() {
        return gui;
    }
    public boolean nextElement(){
        int begin=listBeginIndex.get(listBeginIndex.size()-1);
        if(begin+7>=elements.size())return false;
        listBeginIndex.set(listBeginIndex.size()-1,begin+1);
        renderElements();
        return true;
    }
    public boolean previousElement(){
        int begin=listBeginIndex.get(listBeginIndex.size()-1);
        if(begin==0)return false;
        listBeginIndex.set(listBeginIndex.size()-1,begin-1);
        renderElements();
        return true;
    }
    public void setElementSlot(int elementSlot) {
        this.elementSlot.add(elementSlot);
    }

    public boolean isDeletingElement() {
        return deletingElement;
    }

    public void setDeletingElement(boolean deletingElement) {
        this.deletingElement = deletingElement;
    }

    public void setNewElement(boolean newElement) {
        this.newElement.add(newElement);
    }
}
