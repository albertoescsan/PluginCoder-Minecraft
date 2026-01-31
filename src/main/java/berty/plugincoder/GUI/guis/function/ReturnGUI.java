package berty.plugincoder.GUI.guis.function;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReturnGUI {

    private PluginCoder plugin;
    private Inventory gui;

    private String instruction;
    public ReturnGUI(PluginCoder plugin){
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
        this.plugin=plugin;
    }
    public void createInventory(){
        String returnTitle = PluginCoder.getCoderGUI().getGuiText("returnValue");
        gui= Bukkit.createInventory(null,45, "§f§l"+returnTitle.toUpperCase());
        PluginCoder.getCoderGUI().createCenteredItemInventory(gui);
    }
    public void updateGUI(String instruction){
        this.instruction=instruction;
        String valueInstruction=instruction.replaceAll("^return\\s*(.*)","$1");
        ItemStack executeItem=!instruction.isEmpty()?(new ItemStack(plugin.getCodeUtils().getVersionedMaterial("FILLED_MAP")))
                :(new ItemStack(plugin.getCodeUtils().getVersionedMaterial("MAP")));
        ItemMeta meta=executeItem.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(valueInstruction));
        executeItem.setItemMeta(meta);
        gui.setItem(22,executeItem);
    }
    public Inventory getGui() {
        return gui;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    public boolean saveChanges(boolean closingInventoryWithoutReturning) {
        String newReturnInstruction="return "+ChatColor.stripColor(gui.getItem(22).getItemMeta().getDisplayName());
        int[] instructionPageSlot=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionIndex();
        //borrar index porque ya se ha guardado el cambio en functionGUI
        PluginCoder.getCoderGUI().getFunctionGUI().getFunctionsIndexes().remove(
                PluginCoder.getCoderGUI().getFunctionGUI().getFunctionsIndexes().size()-1);
        if(newReturnInstruction.equals(instruction))return false;
        ItemStack newInstructionItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(newReturnInstruction);
        PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(instructionPageSlot[0]).setItem(instructionPageSlot[1],newInstructionItem);
        if(closingInventoryWithoutReturning)PluginCoder.getCoderGUI().getFunctionGUI().saveChanges(false);
        return true;
    }

    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
}
