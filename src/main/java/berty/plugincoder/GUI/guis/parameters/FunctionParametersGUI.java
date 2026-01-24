package berty.plugincoder.GUI.guis.parameters;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class FunctionParametersGUI {

    private PluginCoder mainPlugin;
    private Inventory gui;

    private boolean isAddingParam=false;
    private int editingParamSlot=0;

    public FunctionParametersGUI(PluginCoder plugin){
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private void createInventory(){
        gui=Bukkit.createInventory(null,27,"");
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        ItemStack instructionItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
        gui.setItem(4,instructionItem);
        updateInventoryLanguage();
    }
    private void updateInventoryLanguage(){
        ItemStack addParam=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta=addParam.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addParameter"));
        addParam.setItemMeta(meta);
        ItemStack removeParam=new ItemStack(Material.LAVA_BUCKET);
        meta=removeParam.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteLastParameter"));
        removeParam.setItemMeta(meta);
        gui.setItem(21,addParam);gui.setItem(23,removeParam);
    }
    public void updateInventory(String function) {
        List<String> params= mainPlugin.getCodeExecuter().getStringParameters(function);
        for(int i=0;i<7;i++){
            gui.setItem(10+i,null);
            if(i>=params.size())continue;
            ItemStack paramItem=PluginCoder.getCoderGUI().getObjectGUI().getPropertyItem(params.get(i),"");
            gui.setItem(10+i,paramItem);
        }
        ItemStack sign=gui.getItem(4);
        ItemMeta meta=sign.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(function));
        sign.setItemMeta(meta);
    }
    public void updateFunctionSign(){
        ItemStack sign=gui.getItem(4);
        ItemMeta meta=sign.getItemMeta();
        String functionName=ChatColor.stripColor(meta.getDisplayName()).trim().replaceAll("^([^(]+)\\((.+)$","$1");
        String params="";
        for(int i=0;i<7;i++){
            ItemStack paramItem=gui.getItem(10+i);
            if(paramItem==null)break;
            params+=ChatColor.stripColor(paramItem.getItemMeta().getDisplayName()).trim()+",";
        }
        String functionHead=params.isEmpty()?functionName:functionName+"("+params.substring(0,params.length()-1)+")";
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(functionHead));
        sign.setItemMeta(meta);
    }
    public void saveChanges(boolean closingInventoryWithoutReturning) {
        String functionWithNewParams=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        PluginCoder.getCoderGUI().getFunctionGUI().saveToFunctionGUI(functionWithNewParams,closingInventoryWithoutReturning);

    }
    public void returnPage(Player p) {
        saveChanges(false);
        p.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getLastPageOpened()));
    }

    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public Inventory getGUI() {
        return gui;
    }

    public void addParam(Player p,int paramSlot) {
        p.closeInventory();
        isAddingParam=true;
        editingParamSlot=paramSlot;
        p.sendMessage(ChatColor.YELLOW+"Write down parameter name");//TODO traducir
    }
    public void deleteLastParam() {
        if(gui.getItem(10)==null)return;
        boolean deleted=false;
        for(int i=1;i<7;i++){
            if(gui.getItem(10+i)==null){
                gui.setItem(9+i,null);
                deleted=true;break;
            }
        }
        if(!deleted)gui.setItem(16,null);
        updateFunctionSign();
    }
    public boolean isAddingParam() {
        return isAddingParam;
    }

    public void setAddingParam(boolean addingParam) {
        isAddingParam = addingParam;
    }

    public int getEditingParamSlot() {
        return editingParamSlot;
    }
}
