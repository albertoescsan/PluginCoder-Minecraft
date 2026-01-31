package berty.plugincoder.GUI.guis.conditions;

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
import java.util.stream.Collectors;
public class CheckObjectTypeGUI {
    private PluginCoder plugin;
    private Inventory gui;
    private List<String> objectTypes=new ArrayList<>();
    private List<String> renderedInstructions=new ArrayList<>();
    private int startIndex=0;
    public CheckObjectTypeGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            createInventory();
            objectTypes.add("Console");objectTypes.add("Entity");objectTypes.add("Player");
            for(PluginObject object:plugin.getSelectedPlugin().getObjects()) objectTypes.add(object.getName());
            for(String constructor:plugin.getConstructorTranslator().keySet())objectTypes.add(constructor);
            sortObjectTypes();
        }, 2);
    }

    //usar en la creacion de Objetos
    public void addObjectToTypeList(String objectType){
        objectTypes.add(objectType);
    }
    public void sortObjectTypes() {
        objectTypes.stream().sorted().collect(Collectors.toList());
    }
    public void removeObjectToTypeList(String objectType){
        objectTypes.remove(objectType);
    }
    private void createInventory(){
        gui= Bukkit.createInventory(null,45," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,false);
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        ItemMeta meta=instructionItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        instructionItem.setItemMeta(meta);
        gui.setItem(4,instructionItem);
        ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        gui.setItem(11,negro);gui.setItem(13,negro);gui.setItem(15,negro);
        negro.setItemMeta(meta);
        ItemStack I=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/5c99dfb2704e1bd6e7facfb43b3e6fbabaf16ebc7e1fab07417a6c464e1d");
        meta=I.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"I");
        I.setItemMeta(meta);
        ItemStack S=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/d710138416528889815548b4623d28d86bbbae5619d69cd9dbc5ad6b43744");
        meta=S.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"S");
        S.setItemMeta(meta);
        gui.setItem(12,I);gui.setItem(14,S);
        ItemStack backItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5");
        meta=backItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        backItem.setItemMeta(meta);
        ItemStack nextItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311");
        meta=nextItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        nextItem.setItemMeta(meta);
        gui.setItem(36,backItem);gui.setItem(44,nextItem);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
    }
    public void updateInventory(String instruction){
        renderedInstructions.add(instruction);
        updateGUI(instruction);
    }
    private void updateGUIWithPreviousInstruction(){
        updateInventory(renderedInstructions.get(renderedInstructions.size()-1));
    }
    private void updateGUI(String instruction){
        startIndex=0;
        String object=""; String type="";
        String[] contents=instruction.replaceAll("^(.*)\\s*is ([^{}()]*)$","$1|$2").split("\\|");
        if(contents.length==2){
           object=contents[0];
           type=contents[1];
        }
        ItemStack vars=new ItemStack(Material.CHEST);
        ItemMeta meta=vars.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+object);
        vars.setItemMeta(meta);
        gui.setItem(10,vars);
        ItemStack typeItem=new ItemStack(Material.NAME_TAG);
        meta =typeItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+type);
        typeItem.setItemMeta(meta);
        gui.setItem(16,typeItem);
        updateTypesBar();
        updateInstructionItem();
    }
    private void updateTypesBar(){
        for(int i=0;i<7;i++)gui.setItem(28+i,null);
        for(int i=0;i<7;i++){
            if(objectTypes.size()==i)break;
            String objectName=objectTypes.get(i+startIndex);
            ItemStack objectItem=new ItemStack(Material.NAME_TAG);
            ItemMeta meta =objectItem.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW+objectName);
            objectItem.setItemMeta(meta);
            gui.setItem(28+i,objectItem);
        }
    }
    public Inventory getGUI() {
        return gui;
    }
    public boolean nextObjectType(){
        if(startIndex+7<objectTypes.size())startIndex++;
        else return false;
        updateTypesBar();
        return true;
    }

    public boolean previousObjectType(){
        if(startIndex>0)startIndex--;
        else return false;
        updateTypesBar();
        return true;
    }
    public void updateInstructionItem(){
        String objectInstruction=ChatColor.stripColor(gui.getItem(10).getItemMeta().getDisplayName()).trim();
        String objectTypeName=ChatColor.stripColor(gui.getItem(16).getItemMeta().getDisplayName()).trim();
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        ItemMeta meta=instructionItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+objectInstruction+" is "+objectTypeName));
        instructionItem.setItemMeta(meta);
        gui.setItem(4,instructionItem);
    }
    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }

    public void saveChanges(boolean closingInventoryWithoutReturning) {
        renderedInstructions.remove(renderedInstructions.size()-1);
        String newInstruction=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        if(newInstruction.trim().equals("is"))return;
        PluginCoder.getCoderGUI().getConditionsGUI().saveToConditionsGUI(newInstruction,closingInventoryWithoutReturning);
    }
    public void saveToCheckObjectTypeGUI(String newInstruction,boolean closingInventoryWithoutReturning){
        updateGUIWithPreviousInstruction();
        ItemStack instructionItem=new ItemStack(Material.CHEST);
        ItemMeta meta=instructionItem.getItemMeta();
        meta.setDisplayName("§6"+PluginCoder.getCoderGUI().putTextColor(newInstruction));
        instructionItem.setItemMeta(meta);
        gui.setItem(10,instructionItem);
        updateInstructionItem();
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
    public void prepareToNextGUI(){
        renderedInstructions.set(renderedInstructions.size()-1,ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim());
    }
}
