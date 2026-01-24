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

public class DictConstructorGUI {

    private PluginCoder plugin;
    private Inventory gui;
    private List<String> keyElements=new ArrayList<>();
    private List<String> valueElements=new ArrayList<>();
    private List<String> previousDicts=new ArrayList<>();
    private List<Integer> elementSlot=new ArrayList<>();
    private boolean deletingElement;
    private List<Boolean> newElement=new ArrayList<>();
    private List<Boolean> isKey=new ArrayList<>();
    private List<Integer> dictBeginIndex=new ArrayList<>();
    public DictConstructorGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    private void createInventory() {
        gui=Bukkit.createInventory(null,36," ");
        PluginCoder.getCoderGUI().createInventoryBase(gui,true);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        gui.setItem(27,PluginCoder.getCoderGUI().getBackItem());
        gui.setItem(35,PluginCoder.getCoderGUI().getNextItem());
        ItemStack delete=new ItemStack(deletingElement?Material.LAVA_BUCKET:Material.BUCKET);
        ItemMeta meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteElement"));
        delete.setItemMeta(meta);
        gui.setItem(31,delete);
        ItemStack addKey=new ItemStack(plugin.getVersionNumber()<13? Material.getMaterial("EMPTY_MAP"):Material.MAP);
        meta= addKey.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addKey"));
        addKey.setItemMeta(meta);
        gui.setItem(29,addKey);
        ItemStack addValue=new ItemStack(plugin.getVersionNumber()<13? Material.getMaterial("EMPTY_MAP"):Material.MAP);
        meta= addValue.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addValue"));
        addValue.setItemMeta(meta);
        gui.setItem(33,addValue);
    }

    private void updateGUIWithPreviousInstruction(){
        updateGUIContent(previousDicts.get(previousDicts.size()-1));
    }
    public void updateGUI(String instruction) {
        deletingElement=false;
        dictBeginIndex.add(0);
        previousDicts.add(instruction);
        updateGUIContent(instruction);
    }
    public void updateGUIContent(String instruction) {
        keyElements.clear();
        valueElements.clear();
        if(!(instruction.startsWith("{")&&instruction.endsWith("}")))instruction="{}";
        for(String keyValue:plugin.getCodeExecuter().getStringParameters("("+instruction.substring(1,instruction.length()-1)+")")){
            String[] kvlist=plugin.getCodeUtils().getElementsBySeparator(keyValue,':');
            keyElements.add(kvlist[0]);valueElements.add(kvlist[1]);
        }
        renderElements();
        updateDictSign();
    }

    public void saveChanges(boolean closingInventoryWithoutReturning){
        String dict=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        previousDicts.remove(previousDicts.size()-1);
        PluginCoder.getCoderGUI().getConstructorsGUI().saveToConstructorsGUI(dict,closingInventoryWithoutReturning);
    }
    public void saveToDictConstrutorGUI(String instruction,boolean closingInventoryWithoutReturning){
        updateGUIWithPreviousInstruction();
        if(newElement.remove(newElement.size()-1)){
            if(isKey.remove(isKey.size()-1))keyElements.add(instruction);
            else  valueElements.add(instruction);
            renderElements();
        }else{
            int invSlot=this.elementSlot.remove(this.elementSlot.size()-1);
            gui.setItem(invSlot,PluginCoder.getCoderGUI().getListConstructorGUI().getListElementItem(instruction));
            if(isKey.remove(isKey.size()-1))keyElements.set(dictBeginIndex.get(dictBeginIndex.size()-1)+invSlot-10,instruction);
            else valueElements.set(dictBeginIndex.get(dictBeginIndex.size()-1)+invSlot-19,instruction);
        }
        updateDictSign();
        String dict=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        previousDicts.set(previousDicts.size()-1,dict);
        if(closingInventoryWithoutReturning)saveChanges(true);

    }
    private void renderElements() {
        for(int i=0;i<7;i++){
            gui.setItem(10+i,null);
            gui.setItem(19+i,null);
        }
        int begin=dictBeginIndex.get(dictBeginIndex.size()-1);
        for(int i=0;i<7;i++){
            if(i+begin>=keyElements.size()&&i+begin>=valueElements.size())return;

            if(i+begin<keyElements.size())gui.setItem(10+i,PluginCoder.getCoderGUI().getListConstructorGUI().getListElementItem(keyElements.get(i+begin)));
            if(i+begin<valueElements.size())gui.setItem(19+i,PluginCoder.getCoderGUI().getListConstructorGUI().getListElementItem(valueElements.get(i+begin)));
        }
    }
    private void updateDictSign(){
       String dictInstruction="{";
        for(int i=0;i<Math.max(keyElements.size(),valueElements.size());i++){
            String key=keyElements.size()>i?keyElements.get(i):"null";
            String value=valueElements.size()>i?valueElements.get(i):"null";
            dictInstruction+=key+":"+value+",";
        }
       if(dictInstruction.endsWith(","))dictInstruction=dictInstruction.substring(0,dictInstruction.length()-1);
        dictInstruction+="}";
       ItemStack sign=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
       ItemMeta meta=sign.getItemMeta();
       meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(dictInstruction));
       sign.setItemMeta(meta);
       gui.setItem(4,sign);

    }

    public void removeElement(int invSlot) {
        if(invSlot<18){
            int index=invSlot-10+dictBeginIndex.get(dictBeginIndex.size()-1);
            keyElements.remove(index);
        }else{
            int index=invSlot-19+dictBeginIndex.get(dictBeginIndex.size()-1);
            valueElements.remove(index);
        }
        renderElements();
        updateDictSign();
        String dict=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        previousDicts.set(previousDicts.size()-1,dict);
    }
    public Inventory getGUI() {
        return gui;
    }
    public boolean nextElement(){
        int begin=dictBeginIndex.get(dictBeginIndex.size()-1);
        if(begin+7>=keyElements.size()&&begin+7>=valueElements.size())return false;
        dictBeginIndex.set(dictBeginIndex.size()-1,begin+1);
        renderElements();
        return true;
    }
    public boolean previousElement(){
        int begin=dictBeginIndex.get(dictBeginIndex.size()-1);
        if(begin==0)return false;
        dictBeginIndex.set(dictBeginIndex.size()-1,begin-1);
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

    public void setIsKey(boolean b) {isKey.add(b);}
}
