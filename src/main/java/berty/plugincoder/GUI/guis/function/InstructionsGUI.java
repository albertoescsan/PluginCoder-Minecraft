package berty.plugincoder.GUI.guis.function;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InstructionsGUI {
    private PluginCoder plugin;
    private Inventory gui;
    private Inventory conditionalsGui;
    private Inventory loopsGui;
    public InstructionsGUI(PluginCoder plugin){
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateGUI(), 2);
        this.plugin=plugin;
    }
    public void updateGUI(){
        String guiTitle= PluginCoder.getCoderGUI().getGuiText("addInstruction").toUpperCase();
        String conditionalTitle=PluginCoder.getCoderGUI().getGuiText("conditionals");
        String loopsTitle = PluginCoder.getCoderGUI().getGuiText("loops");
        gui= Bukkit.createInventory(null,27, ChatColor.translateAlternateColorCodes('&',"&f&l"+guiTitle));
        conditionalsGui= Bukkit.createInventory(null,27, ChatColor.translateAlternateColorCodes('&',"&f&l"+conditionalTitle.toUpperCase()));
        loopsGui= Bukkit.createInventory(null,27, ChatColor.translateAlternateColorCodes('&',"&f&l"+loopsTitle.toUpperCase()));
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        PluginCoder.getCoderGUI().createInventoryBase(conditionalsGui,false);
        PluginCoder.getCoderGUI().createInventoryBase(loopsGui,false);
        ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta=negro.getItemMeta();
        meta.setDisplayName(" ");
        negro.setItemMeta(meta);
        ItemStack variable=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        for(int i=10;i<17;i++){
            conditionalsGui.setItem(i,negro);loopsGui.setItem(i,negro);
        }
        meta=variable.getItemMeta();
        String varTitle= PluginCoder.getCoderGUI().getGuiText("declareVariable");
        meta.setDisplayName(ChatColor.WHITE+varTitle);
        variable.setItemMeta(meta);
        ItemStack execute=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        meta=execute.getItemMeta();
        String executeTitle=PluginCoder.getCoderGUI().getGuiText("executeInstruction");
        meta.setDisplayName(ChatColor.WHITE+executeTitle);
        execute.setItemMeta(meta);
        ItemStack conditional=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=conditional.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+conditionalTitle);
        conditional.setItemMeta(meta);
        ItemStack loopsItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=loopsItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+loopsTitle);
        loopsItem.setItemMeta(meta);
        ItemStack delayFunction=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=delayFunction.getItemMeta();
        String delayFunctionTitle=PluginCoder.getCoderGUI().getGuiText("delayFunction");
        meta.setDisplayName(ChatColor.WHITE+delayFunctionTitle);
        delayFunction.setItemMeta(meta);
        ItemStack repeatFunction=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=repeatFunction.getItemMeta();
        String repeatFunctionTitle=PluginCoder.getCoderGUI().getGuiText("repeatFunction");
        meta.setDisplayName(ChatColor.WHITE+repeatFunctionTitle);
        repeatFunction.setItemMeta(meta);
        ItemStack returnItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        meta=returnItem.getItemMeta();
        String returnTitle = PluginCoder.getCoderGUI().getGuiText("returnValue");
        meta.setDisplayName(ChatColor.WHITE+returnTitle);
        returnItem.setItemMeta(meta);
        gui.setItem(10,variable);gui.setItem(11,execute);gui.setItem(12,conditional);gui.setItem(13,loopsItem);
        gui.setItem(14,delayFunction);gui.setItem(15,repeatFunction);gui.setItem(16,returnItem);
        ItemStack forLoop=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=forLoop.getItemMeta();
        String forLoopTitle=PluginCoder.getCoderGUI().getGuiText("forLoop");
        meta.setDisplayName(ChatColor.WHITE+forLoopTitle);
        forLoop.setItemMeta(meta);
        ItemStack whileLoop=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=whileLoop.getItemMeta();
        String whileLoopTitle=PluginCoder.getCoderGUI().getGuiText("whileLoop");
        meta.setDisplayName(ChatColor.WHITE+whileLoopTitle);
        whileLoop.setItemMeta(meta);
        loopsGui.setItem(12,forLoop);loopsGui.setItem(14,whileLoop);

        ItemStack ifConditional=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=ifConditional.getItemMeta();
        String ifTitle=PluginCoder.getCoderGUI().getGuiText("ifConditional");
        meta.setDisplayName(ChatColor.WHITE+ifTitle);
        ifConditional.setItemMeta(meta);
        ItemStack elseifConditional=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=elseifConditional.getItemMeta();
        String elseifTitle=PluginCoder.getCoderGUI().getGuiText("elseIfConditional");
        meta.setDisplayName(ChatColor.WHITE+elseifTitle);
        elseifConditional.setItemMeta(meta);
        ItemStack elseConditional=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=elseConditional.getItemMeta();
        String elseTitle=PluginCoder.getCoderGUI().getGuiText("elseConditional");
        meta.setDisplayName(ChatColor.WHITE+elseTitle);
        elseConditional.setItemMeta(meta);
        conditionalsGui.setItem(11,ifConditional);conditionalsGui.setItem(13,elseifConditional);conditionalsGui.setItem(15,elseConditional);
    }

    public Inventory getGui() {
        return gui;
    }

    public Inventory getConditionalsGui() {
        return conditionalsGui;
    }

    public Inventory getLoopsGui() {
        return loopsGui;
    }
}
