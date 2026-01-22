package berty.plugincoder.GUI.guis.constructor;

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

public class ConstructorsGUI {
    private PluginCoder plugin;
    private Inventory gui;
    private List<Inventory> previousInvs=new ArrayList<>();
    public ConstructorsGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private void createInventory() {
        gui=Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
        gui.setItem(4,instructionItem);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage() {
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        ItemStack objects=new ItemStack(Material.CHEST);
        ItemMeta meta=objects.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("objectsTitle"));
        objects.setItemMeta(meta);
        gui.setItem(49,objects);
        ItemStack list=new ItemStack(Material.BOOK);
        meta= list.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("list"));
        list.setItemMeta(meta);
        ItemStack dict=new ItemStack(Material.BOOK);
        meta= dict.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("dict"));
        dict.setItemMeta(meta);
        gui.setItem(47,list); gui.setItem(51,dict);
        ItemStack inv=new ItemStack(Material.CHEST);
        meta= inv.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+"Inventory");//TODO traducir
        inv.setItemMeta(meta);
        gui.setItem(10,inv);
        ItemStack itemStack=new ItemStack(Material.DIAMOND);
        meta= itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+"ItemStack");//TODO traducir
        itemStack.setItemMeta(meta);
        gui.setItem(11,itemStack);
        ItemStack location=new ItemStack(Material.COMPASS);
        meta= location.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+"Location");//TODO traducir
        location.setItemMeta(meta);
        gui.setItem(12,location);

    }
    public void updateInventory(Inventory inv,String instruction) {
        previousInvs.add(inv);
        updateInstructionSign(instruction);
        ItemStack list=gui.getItem(47);
        ItemMeta meta=list.getItemMeta();
        List<String> lore=new ArrayList<>();
        if(instruction.startsWith("[")&&instruction.endsWith("]"))lore.add(ChatColor.WHITE+ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(instruction)));
        else lore.add(ChatColor.WHITE+"[]");
        meta.setLore(new ArrayList<>(lore));
        list.setItemMeta(meta);
        ItemStack dict=gui.getItem(51);
        meta=dict.getItemMeta();
        lore=new ArrayList<>();
        if(instruction.startsWith("{")&&instruction.endsWith("}"))lore.add(ChatColor.WHITE+ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(instruction)));
        else lore.add(ChatColor.WHITE+"{}");
        meta.setLore(new ArrayList<>(lore));
        dict.setItemMeta(meta);
    }
    public void updateInstructionSign(String instruction){
        ItemStack instructionItem=gui.getItem(4);
        ItemMeta meta=instructionItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(instruction)));
        String parametersEditText = PluginCoder.getCoderGUI().getGuiText("clickEditParams");
        if (classConstructorHasParams(instruction)) {
            List<String> lore=new ArrayList<>();
            lore.add(ChatColor.YELLOW+parametersEditText);
            meta.setLore(lore);
        }
        instructionItem.setItemMeta(meta);
    }
    public void saveChanges(boolean closingInventoryWithoutReturning){
        Inventory previousInv=previousInvs.remove(previousInvs.size()-1);

        String newInstruction=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        if(previousInv.equals(PluginCoder.getCoderGUI().getSetValueGUI().getGUI())){
            PluginCoder.getCoderGUI().getSetValueGUI().saveToSetGuiPage(newInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI())){
            PluginCoder.getCoderGUI().getParametersGUI().saveToParametersGUI(newInstruction,closingInventoryWithoutReturning);
        }
        //TODO añadir mas inventarios
    }
    public void saveToConstructorsGUI(String newInstruction,boolean closingInventoryWithoutReturning){
        updateInstructionSign(newInstruction);
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
    public boolean classConstructorHasParams(String instruction){
        if(instruction.trim().isEmpty())return false;
        String className=instruction.replaceAll("^([a-zA-Z]+)\\((.*)\\)$","$1");
        if(plugin.getConstructorTranslator().containsKey(className))return true;//TODO cambiar si algun constructor no tiene parametros
        else{
            for(PluginObject object:plugin.getSelectedPlugin().getObjects()){
                if(!className.equals(object.getName()))continue;
                for(String constructor:object.getConstructors()){
                    if(!plugin.getCodeExecuter().getStringParameters(
                            constructor.replaceAll("^([^{]+)\\{(.*)}$","$1")).isEmpty())return true;
                }
            }
        }
        return false;
    }
    public void returnPage(Player p) {
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        saveChanges(false);
        p.openInventory(previousInv);
    }
    public Inventory getGUI() {
        return gui;
    }
}
