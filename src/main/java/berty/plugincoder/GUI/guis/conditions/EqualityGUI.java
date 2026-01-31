package berty.plugincoder.GUI.guis.conditions;

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

public class EqualityGUI {
    private PluginCoder plugin;
    private Inventory gui;
    private List<String> renderedInstructions=new ArrayList<>();
    private List<Integer> selectedInstructionSlot=new ArrayList<>();
    public EqualityGUI(PluginCoder plugin) {
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    private void createInventory() {
        gui= Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,false);
        ItemMeta meta;
        ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        for(int i=0;i<7;i+=2){
            gui.setItem(10+i,negro);gui.setItem(28+i,negro);
        }
        gui.setItem(37,negro);gui.setItem(38,negro);gui.setItem(42,negro);gui.setItem(43,negro);
        ItemStack lessThan=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d");
        meta=lessThan.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"<");
        lessThan.setItemMeta(meta);
        ItemStack equal=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/d773155306c9d2d58b149673951cbc6666aef87b8f873538fc85745f01b51");
        meta=equal.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"=");
        equal.setItemMeta(meta);
        ItemStack biggerThan=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287");
        meta=biggerThan.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+">");
        biggerThan.setItemMeta(meta);
        gui.setItem(39,lessThan);gui.setItem(40,equal);gui.setItem(41,biggerThan);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        gui.setItem(4,instructionItem);
        ItemStack text=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("WRITABLE_BOOK"));
        ItemMeta meta=text.getItemMeta();
        meta.setDisplayName(ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("text"));
        text.setItemMeta(meta);
        ItemStack math=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/1d1a3c96562348527d5798f291609281f72e16d611f1a76c0fa7abe043665");
        meta=math.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("mathTitle"));
        math.setItemMeta(meta);
        ItemStack vars=new ItemStack(Material.CHEST);
        meta=vars.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("variablesTitle"));
        vars.setItemMeta(meta);
        gui.setItem(29,text);gui.setItem(31,math);gui.setItem(33,vars);
        ItemStack delete=new ItemStack(Material.LAVA_BUCKET);
        meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteInstruction"));
        delete.setItemMeta(meta);
        gui.setItem(2,delete);gui.setItem(6,delete);
    }
    public void updateGUI(String instruction){
        renderedInstructions.add(instruction);
        selectedInstructionSlot.add(10);
        updateInventory(instruction);
        selectInstructionItem(11);
    }
    private void updateGUIWithPreviousInstruction(){
        updateInventory(renderedInstructions.get(renderedInstructions.size()-1));
    }
    private void updateInventory(String instruction){
        int parentesisCount=0;
        boolean operatorFound=false;
        String element1="";
        String element2="";
        String operator="";
        for(Character c:instruction.toCharArray()){
            if(c.equals('('))parentesisCount++;
            else if(c.equals(')'))parentesisCount++;
            if(parentesisCount==0){
                if(c.equals('=')||c.equals('<')||c.equals('>')){
                    operatorFound=true;operator+=c;continue;
                }
            }
            if(!operatorFound)element1+=c;
            else element2+=c;
        }
        element1=element1.trim();
        if(element1.isEmpty())element1="null";
        element2=element2.trim();
        if(element2.isEmpty())element2="null";
        operator=operator.trim();
        if(operator.isEmpty())operator="=";
        ItemStack element1Item=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
        ItemMeta meta=element1Item.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(element1));
        element1Item.setItemMeta(meta);
        ItemStack element2Item=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
        meta=element2Item.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(element2));
        element2Item.setItemMeta(meta);
        gui.setItem(11,element1Item);gui.setItem(15,element2Item);
        if(operator.length()==2){
            gui.setItem(12,getOperatorItem(operator.charAt(0)+""));
            gui.setItem(13,getOperatorItem(operator.charAt(1)+""));
        }else{
            gui.setItem(13,getOperatorItem(operator));
        }
        updateEqualityInstruction();
    }
    public ItemStack getOperatorItem(String operator){
        ItemStack itemStack;
        if(operator.equals("<"))itemStack= gui.getItem(39);
        else if(operator.equals("="))itemStack= gui.getItem(40);
        else itemStack= gui.getItem(41);
        ItemMeta meta=itemStack.getItemMeta();
        meta.setLore(new ArrayList<>());
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    public Inventory getGUI() {
        return gui;
    }
    public void selectInstructionItem(int slot){
        int deselectSlot=slot==15?11:15;
        ItemStack instructionItem=gui.getItem(slot);
        ItemMeta meta=instructionItem.getItemMeta();
        meta.addEnchant(Enchantment.values()[0],1,false);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        instructionItem.setItemMeta(meta);
        gui.setItem(slot,instructionItem.clone());
        instructionItem=gui.getItem(deselectSlot);
        meta=instructionItem.getItemMeta();
        meta.removeEnchant(Enchantment.values()[0]);
        instructionItem.setItemMeta(meta);
        gui.setItem(deselectSlot,instructionItem);
        selectedInstructionSlot.set(selectedInstructionSlot.size()-1,slot);
    }
    public void saveChanges(boolean closingInventoryWithoutReturning) {
        renderedInstructions.remove(renderedInstructions.size()-1);
        selectedInstructionSlot.remove(selectedInstructionSlot.size()-1);
        String newInstruction=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        if(newInstruction.equals("=")||newInstruction.equals("<")||newInstruction.equals(">")
        ||newInstruction.equals("=<")||newInstruction.equals("=>"))return;
        PluginCoder.getCoderGUI().getConditionsGUI().saveToConditionsGUI(newInstruction,closingInventoryWithoutReturning);
    }
    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public void saveToEqualityGUI(String instruction,boolean closingInventoryWithoutReturning){
        updateGUIWithPreviousInstruction();
        if(instruction.trim().isEmpty())instruction="null";
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
        ItemMeta meta=instructionItem.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(instruction));
        meta.addEnchant(Enchantment.values()[0],1,false);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        instructionItem.setItemMeta(meta);
        gui.setItem(selectedInstructionSlot.get(selectedInstructionSlot.size()-1),instructionItem);
        updateEqualityInstruction();
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
    public void prepareToNextGUI(){
        renderedInstructions.set(renderedInstructions.size()-1,ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim());
    }
    public void updateEqualityInstruction(){
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        String element1=ChatColor.stripColor(gui.getItem(11).getItemMeta().getDisplayName()).trim();
        String element2=ChatColor.stripColor(gui.getItem(15).getItemMeta().getDisplayName()).trim();
        String operator=ChatColor.stripColor(gui.getItem(13).getItemMeta().getDisplayName()).trim();
        ItemStack subOperator=gui.getItem(12);
        if(subOperator.getType().toString().equals("PLAYER_HEAD")||subOperator.getType().toString().equals("SKULL_ITEM")){
            operator=ChatColor.stripColor(subOperator.getItemMeta().getDisplayName()).trim()+operator;
        }
        ItemMeta meta=instructionItem.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(element1+operator+element2));
        instructionItem.setItemMeta(meta);
        gui.setItem(4,instructionItem);
        if(operator.equals("<")||operator.equals(">")){
            //poner descripción de shift click
            ItemStack equalsItem=gui.getItem(40);
            meta=equalsItem.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<String> lore=new ArrayList<>();
            String shiftClick=PluginCoder.getCoderGUI().getGuiText("scEquality").replace("operator",operator);
            lore.add(ChatColor.GRAY+shiftClick);
            meta.setLore(lore);
            equalsItem.setItemMeta(meta);
        }
    }
    public int getSelectedInstructionSlot() {
        return selectedInstructionSlot.get(selectedInstructionSlot.size()-1);
    }
}
