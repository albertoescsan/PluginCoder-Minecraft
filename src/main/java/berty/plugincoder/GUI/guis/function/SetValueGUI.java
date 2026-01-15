package berty.plugincoder.GUI.guis.function;

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

public class SetValueGUI {
    private PluginCoder plugin;
    private Inventory gui;

    private List<String> renderedInstructions=new ArrayList<>();
    private List<Inventory> previousInvs=new ArrayList<>();
    public Inventory getGUI() {
        return gui;
    }

    public SetValueGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    public void createInventory() {
        gui=Bukkit.createInventory(null,45," ");
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        PluginCoder.getCoderGUI().createCenteredItemInventory(gui);
        ItemStack delete=new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteInstruction"));
        delete.setItemMeta(meta);
        ItemStack text=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=text.getItemMeta();
        meta.setDisplayName(ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("text"));
        text.setItemMeta(meta);
        ItemStack math=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/1d1a3c96562348527d5798f291609281f72e16d611f1a76c0fa7abe043665");
        meta=math.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("mathTitle"));
        math.setItemMeta(meta);
        ItemStack bool=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/bc8ea1f51f253ff5142ca11ae45193a4ad8c3ab5e9c6eec8ba7a4fcb7bac40");
        meta=bool.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("conditionsTitle"));
        bool.setItemMeta(meta);
        ItemStack vars=new ItemStack(Material.CHEST);
        meta=vars.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("variablesTitle"));
        vars.setItemMeta(meta);
        ItemStack newObject=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.CRAFTING_TABLE));
        meta=newObject.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("newObject"));
        newObject.setItemMeta(meta);
        gui.setItem(4,delete);
        gui.setItem(38,text);gui.setItem(39,math);gui.setItem(40,bool);gui.setItem(41,vars);gui.setItem(42,newObject);
    }
    public void updateGUI(String instruction,Inventory inventory){
        renderedInstructions.add(instruction);
        previousInvs.add(inventory);
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta=instructionItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(instruction)));
        instructionItem.setItemMeta(meta);
        gui.setItem(22,instructionItem);
    }

    public void returnPage(Player p) {
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        saveChanges(false);
        p.openInventory(previousInv);
    }

    public boolean saveChanges(boolean closingInventoryWithoutReturning) {
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        previousInvs.remove(previousInvs.size()-1);
        String oldExecuteInstruction=renderedInstructions.get(renderedInstructions.size()-1);
        renderedInstructions.remove(renderedInstructions.size()-1);
        String newInstruction=ChatColor.stripColor(gui.getItem(22).getItemMeta().getDisplayName()).trim();
        if(newInstruction.equals(oldExecuteInstruction))return false;
        if(previousInv.equals(PluginCoder.getCoderGUI().getVariableGUI().getGui())){
            ItemStack executeItem=!newInstruction.isEmpty()?(new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP)))
                    :(new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.MAP)));
            ItemMeta meta=executeItem.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(newInstruction)));
            executeItem.setItemMeta(meta);
            PluginCoder.getCoderGUI().getVariableGUI().getGui().setItem(24,executeItem);
            if(closingInventoryWithoutReturning)PluginCoder.getCoderGUI().getVariableGUI().saveChanges(true);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getReturnGUI().getGui())){
            ItemStack returnItem=PluginCoder.getCoderGUI().getReturnGUI().getGui().getItem(22);
            ItemMeta meta=returnItem.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+newInstruction);
            returnItem.setItemMeta(meta);
            if(closingInventoryWithoutReturning)PluginCoder.getCoderGUI().getReturnGUI().saveChanges(true);
        }else if(PluginCoder.getCoderGUI().getListConstructorGUI().getGUI().equals(previousInv)){
            PluginCoder.getCoderGUI().getListConstructorGUI().saveToListConstrutorGUI(newInstruction,closingInventoryWithoutReturning);
        }else if(PluginCoder.getCoderGUI().getDictConstructorGUI().getGUI().equals(previousInv)){
            PluginCoder.getCoderGUI().getDictConstructorGUI().saveToDictConstrutorGUI(newInstruction,closingInventoryWithoutReturning);
        }else{
            //TODO añadir mas inventarios
        }
        return true;
    }

    public void returnHome(Player p) {
        saveChanges(true);
        renderedInstructions.clear();previousInvs.clear();
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public void saveToSetGuiPage(String newInstruction,boolean closingInventoryWithoutReturning){
        if(newInstruction.trim().isEmpty())newInstruction="null";
        ItemStack executeItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta=executeItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(newInstruction)));
        executeItem.setItemMeta(meta);
        gui.setItem(22,executeItem);
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
}
