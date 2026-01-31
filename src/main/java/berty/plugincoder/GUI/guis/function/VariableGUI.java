package berty.plugincoder.GUI.guis.function;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class VariableGUI {
    private PluginCoder plugin;
    private Inventory gui;

    private String varInstruction;
    private boolean editingVarName=false;

    private boolean newVar=false;
    private boolean previousInvIsFunctionGUI=true;
    public Inventory getGui() {
        return gui;
    }
    public String getVarInstruction() {
        return varInstruction;
    }
    public boolean isEditingVarName() {
        return editingVarName;
    }

    public void setEditingVarName(boolean editingVarName) {
        this.editingVarName = editingVarName;
    }
    public boolean isNewVar() {
        return newVar;
    }

    public void setNewVar(boolean newVar) {
        this.newVar = newVar;
    }

    public VariableGUI(PluginCoder plugin){
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
        this.plugin=plugin;
    }
    public void createInventory(){
        String guiTitle= PluginCoder.getCoderGUI().getGuiText("declareVariable").toUpperCase();
        gui= Bukkit.createInventory(null,45, ChatColor.translateAlternateColorCodes('&',"&f&l"+guiTitle));
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        ItemStack blanco=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("WHITE_STAINED_GLASS_PANE"));
        ItemMeta meta=blanco.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        blanco.setItemMeta(meta);
        gui.setItem(13,blanco);gui.setItem(22,blanco);gui.setItem(31,blanco);
        ItemStack negro=plugin.getVersionNumber()<13?
        new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        PluginCoder.getCoderGUI().createCircleArroundSlot(20,gui,negro);
        PluginCoder.getCoderGUI().createCircleArroundSlot(24,gui,negro);
    }
    public void updateGUI(String instruction,Inventory inventory){
        varInstruction=instruction;
        previousInvIsFunctionGUI=PluginCoder.getCoderGUI().getFunctionGUI().getGUI().stream().anyMatch(inv -> inv.equals(inventory));
        String varName; String exectute;
        varName=instruction.replaceAll("^([^=]+)\\=(.*)$","$1");
        exectute=instruction.replaceAll("^([^=]+)\\=(.*)$","$2");
        ItemStack varNameItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        ItemMeta meta=varNameItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+varName);
        varNameItem.setItemMeta(meta);
        ItemStack executeItem=!exectute.isEmpty()?(new ItemStack(plugin.getCodeUtils().getVersionedMaterial("FILLED_MAP")))
                :(new ItemStack(plugin.getCodeUtils().getVersionedMaterial("MAP")));
        meta=executeItem.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(exectute));
        executeItem.setItemMeta(meta);
        gui.setItem(20,varNameItem);gui.setItem(24,executeItem);
    }
    public void setPlayerToEditVarName(Player p){
        editingVarName=true;
        p.closeInventory();
        p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeVarName"));
    }
    public void returnPage(Player p) {
        if(previousInvIsFunctionGUI)p.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(
                PluginCoder.getCoderGUI().getFunctionGUI().getLastPageOpened()
        ));
        else p.openInventory(PluginCoder.getCoderGUI().getObjectGUI().getGUI());
    }
    public boolean saveChanges(boolean closingInventoryWithoutReturning) {
        String varName=ChatColor.stripColor(gui.getItem(20).getItemMeta().getDisplayName());
        String execute=ChatColor.stripColor(gui.getItem(24).getItemMeta().getDisplayName());
        String newInstruction=varName+"="+execute;
        if(previousInvIsFunctionGUI){
            PluginCoder.getCoderGUI().getFunctionGUI().saveChanges(false);
            List<String> functions=PluginCoder.getCoderGUI().getFunctionGUI().getFunctions();
            String function=functions.get(functions.size()-1);
            int[] instructionPageSlot=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionIndex();
            //borrar index
            PluginCoder.getCoderGUI().getFunctionGUI().getFunctionsIndexes().remove(
                    PluginCoder.getCoderGUI().getFunctionGUI().getFunctionsIndexes().size()-1);
            //no guarda si no hay cambio
            if(newInstruction.equals(varInstruction))return false;
            String oldVarName=varInstruction.replaceAll("^(.+)\\=(.+)$","$1");
            if(!oldVarName.equals(varName)){
                function=replaceFunctionWithNewVar(function,oldVarName,varName);
                PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithPreviousFunction(function);
            }
            ItemStack newInstructionItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(newInstruction);
            PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(instructionPageSlot[0]).setItem(instructionPageSlot[1],newInstructionItem);
            if(closingInventoryWithoutReturning)PluginCoder.getCoderGUI().getFunctionGUI().saveChanges(false);
        }else PluginCoder.getCoderGUI().getObjectGUI().saveToObjectGUI(newInstruction);
        return true;
    }
    public String replaceFunctionWithNewVar(String function,String oldVarName,String varName){
        while(function.matches("(.*)([\\s;]*)"+oldVarName+"([\\s;]*)(.*)")){
            function=function.replaceAll("(.*)([\\s;]*)"+oldVarName+"([\\s;]*)(.*)","$1$2"+varName+"$3$4");
        }
        while(function.matches("(.*)([^.])"+oldVarName+"\\.(.+)")){
            function=function.replaceAll("(.+)([^.])"+oldVarName+"\\.(.+)","$1$2"+varName+".$3");
        }while(function.matches("^(.*)([,(+\\-/*%:\\s])"+oldVarName+"([,)+\\-/*%:\\s])(.*)$")){
            function=function.replaceAll("^(.*)([,(+\\-/*:\\s])"+oldVarName+"([,)+\\-/*:\\s])(.*)$","$1$2"+varName+"$3$4");
        }
        return function;
    }

    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
}
