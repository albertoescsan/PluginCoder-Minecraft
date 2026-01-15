package berty.plugincoder.GUI.guis.math;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class MathGUI {
    private PluginCoder plugin;
    private Inventory gui;

    private List<String> renderedInstructions=new ArrayList<>();
    private List<Inventory> previousInvs=new ArrayList<>();
    private List<String> mathContents=new ArrayList<>();
    private int startContentIndex=0;
    private List<Boolean> isNewNumber=new ArrayList<>();
    private List<Integer> lastIndex=new ArrayList<>();
    public MathGUI(PluginCoder pluginCoder){
        plugin=pluginCoder;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    public void createInventory() {
    gui=Bukkit.createInventory(null,54," ");
    PluginCoder.getCoderGUI().createUpperLineInventory(gui,false);
    ItemStack plus=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/60b55f74681c68283a1c1ce51f1c83b52e2971c91ee34efcb598df3990a7e7");
    ItemMeta meta=plus.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"+");
    plus.setItemMeta(meta);
    ItemStack minus=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/c3e4b533e4ba2dff7c0fa90f67e8bef36428b6cb06c45262631b0b25db85b");
    meta=minus.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"-");
    minus.setItemMeta(meta);
    ItemStack product=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/1d1a3c96562348527d5798f291609281f72e16d611f1a76c0fa7abe043665");
    meta=product.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"x");
    product.setItemMeta(meta);
    ItemStack division=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/57b1791bdc46d8a5c51729e8982fd439bb40513f64b5babee93294efc1c7");
    meta=division.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"/");
    division.setItemMeta(meta);
    ItemStack rest=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/a9f27d54ec5552c2ed8f8e1917e8a21cb98814cbb4bc3643c2f561f9e1e69f");
    meta=rest.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"%");
    rest.setItemMeta(meta);
    ItemStack openParentesis=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/674153bb6a3fa8979a889f99aa0fc84ba9f427ff0d8e487f76fa6c8831d18e9");
    meta=openParentesis.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"(");
    openParentesis.setItemMeta(meta);
    ItemStack closeParentesis=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/9c60e240cda0d9bb315b385119e5a9482e885827baf018e1c03b6a6d612d3f");
    meta=closeParentesis.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+")");
    closeParentesis.setItemMeta(meta);
    gui.setItem(37,plus);gui.setItem(38,minus);gui.setItem(39,product);gui.setItem(40,division);gui.setItem(41,rest);
    gui.setItem(42,openParentesis);gui.setItem(43,closeParentesis);
    ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    meta=negro.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"");
    negro.setItemMeta(meta);
    gui.setItem(28,negro); gui.setItem(29,negro); gui.setItem(31,negro); gui.setItem(33,negro);gui.setItem(34,negro);
    ItemStack backItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5");
    meta=backItem.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"");
    backItem.setItemMeta(meta);
    ItemStack nextItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311");
    meta=nextItem.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE+"");
    nextItem.setItemMeta(meta);
    gui.setItem(18,backItem);gui.setItem(26,nextItem);
    ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
    gui.setItem(4,instructionItem);
    updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        ItemStack vars=new ItemStack(Material.CHEST);
        ItemMeta meta=vars.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("variablesTitle"));
        vars.setItemMeta(meta);
        gui.setItem(32,vars);
        ItemStack insertNumber=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/b01a568eba7e453b55f15545f5e35ffab8791aacf9034afbbbe4bddb21fa50");
        meta=insertNumber.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("insertNumber"));
        insertNumber.setItemMeta(meta);
        gui.setItem(30,insertNumber);
        ItemStack delete=new ItemStack(Material.LAVA_BUCKET);
        meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteLast"));
        delete.setItemMeta(meta);gui.setItem(22,delete);
    }
    public void updateGUI(String instruction,Inventory inventory){
        renderedInstructions.add(instruction);
        previousInvs.add(inventory);
        updateGUI(instruction);
    }
    private void updateGUIWithPreviousInstruction(){
        updateGUI(renderedInstructions.get(renderedInstructions.size()-1));
    }
    private void updateGUI(String instruction){
        updateMathContents(instruction);
        startContentIndex=0;
        updateMathBar();
    }
    public void updateMathBar() {
        PluginCoder.getCoderGUI().updateLinedElementsGUI(gui,mathContents,startContentIndex,
                content->getContentItem((String)content));
    }
    private ItemStack getContentItem(String operation) {
        ItemStack operationItem;
        if(operation.equals("+"))operationItem=gui.getItem(37);
        else if(operation.equals("-"))operationItem=gui.getItem(38);
        else if(operation.equals("*"))operationItem=gui.getItem(39);
        else if(operation.equals("/"))operationItem=gui.getItem(40);
        else if(operation.equals("%"))operationItem=gui.getItem(41);
        else if(operation.equals("("))operationItem=gui.getItem(42);
        else if(operation.equals(")"))operationItem=gui.getItem(43);
        else{
            operationItem=!operation.isEmpty()?(new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP)))
                    :(new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.MAP)));
            ItemMeta meta=operationItem.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(operation)));
            operationItem.setItemMeta(meta);
            return operationItem;
        }
        ItemMeta meta=operationItem.getItemMeta();
        meta.setLore(new ArrayList<>());
        operationItem.setItemMeta(meta);
        return operationItem;
    }

    private void updateMathContents(String instruction) {
        mathContents.clear();
        String element="";
        int parentesisCount=0;
        for(Character c:instruction.toCharArray()){
            if(c.equals('(')){
                if(!element.isEmpty()){
                    parentesisCount++;element+=c;
                }else mathContents.add("(");
            }
            else if(c.equals(')')){
                if(parentesisCount!=0) {
                    parentesisCount--;element+=c;
                }else {
                    if(!element.isEmpty()){
                        mathContents.add(element);element="";
                    }
                    mathContents.add(")");
                }
            }else if((c.equals('+')||c.equals('-')||c.equals('*')||c.equals('/')||c.equals('%'))&&parentesisCount==0){
                if(!element.isEmpty()){
                    mathContents.add(element); element="";
                }
               mathContents.add(String.valueOf(c));
            }else element+=c;
        }
        if(!element.isEmpty())mathContents.add(element);
    }

    public boolean saveChanges(boolean closingInventoryWithoutReturning) {
        Inventory previousInv=previousInvs.remove(previousInvs.size()-1);
        renderedInstructions.remove(renderedInstructions.size()-1);
        String newInstruction=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        //validar numero de parentesis
        int parentesisCount=0;
        for(Character c:newInstruction.toCharArray()){
            if(c.equals('('))parentesisCount++;
            else if(c.equals(')'))parentesisCount--;
        }
        if(parentesisCount!=0){
            ErrorManager.differentNumberOfParentesisOpenClose();
            return false;
        }
        if(previousInv.equals(PluginCoder.getCoderGUI().getSetValueGUI().getGUI())){
            PluginCoder.getCoderGUI().getSetValueGUI().saveToSetGuiPage(newInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI())){
            PluginCoder.getCoderGUI().getEqualityGUI().saveToEqualityGUI(newInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI())){
            PluginCoder.getCoderGUI().getParametersGUI().saveToParametersGUI(newInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getForParametersGUI().getGUI())){
            PluginCoder.getCoderGUI().getForParametersGUI().saveToForParametersGUI(newInstruction,closingInventoryWithoutReturning);
        }else{
            //TODO añadir mas inventarios
        }
        return true;
    }
    public boolean saveToMathGUI(String newInstruction,boolean closingInventoryWithoutReturning){
        updateGUIWithPreviousInstruction();
        if(!executionIsNumber(newInstruction)){
            ErrorManager.executeSequenceIsNotANumber();
            return false;
        }
        if(isNewNumber.remove(isNewNumber.size()-1)) addNewContent(newInstruction);
        else{
            mathContents.set(lastIndex.get(lastIndex.size()-1),newInstruction);
            //delete last index
            lastIndex.remove(lastIndex.size()-1);
        }
        updateMathBar();//actualizar gui
        if(closingInventoryWithoutReturning)saveChanges(true);
        return true;
    }
    public void prepareToNextGUI(){
        renderedInstructions.set(renderedInstructions.size()-1,ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim());
    }
    public Inventory getGUI() {
        return gui;
    }

    public void returnPage(Player p) {
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        saveChanges(false);
        p.openInventory(previousInv);
    }

    public void returnHome(Player p) {
        saveChanges(true);
        isNewNumber.clear();lastIndex.clear();
        mathContents.clear();renderedInstructions.clear();previousInvs.clear();
        PluginCoder.getCoderGUI().returnHome(p,true);
    }

    public boolean nextContent(){
        if(startContentIndex+7<mathContents.size())startContentIndex++;
        else return false;
        updateMathBar();
        return true;
    }
    public boolean previousContent(){
        if(startContentIndex>0)startContentIndex--;
        else return false;
        updateMathBar();
        return true;
    }
    public boolean executionIsNumber(String instruction){
        if(instruction.trim().isEmpty())return true;
        String type=plugin.getExecutionWriterGUI().getTypeOfExecution(instruction);
        if(type==null){
            for(String execution:plugin.getCodeExecuter().getElements(instruction,new String[]{"+","-","*","/","%","(",")"})){
                try {
                    Double.parseDouble(execution);
                }catch (Exception e){
                    String executionType=plugin.getExecutionWriterGUI().getTypeOfExecution(execution);
                    if(executionType==null||!typeIsMath(executionType))return false;
                }
            }
            return true;
        }
        else return typeIsMath(type);
    }
    public boolean typeIsMath(String type){
        return plugin.getCodeExecuter().typeIsMath(type);
    }
    public boolean checkNewContent(String content){
        List<String> mathSymbols=Arrays.asList("+","-","*","/","%");
        if(mathContents.isEmpty())return true;
        String lastContent=mathContents.get(mathContents.size()-1);
        if(lastContent.equals("(")&&(!mathSymbols.contains(content)||content.equals("-")))return true;
        else if(content.equals(")")&&!mathSymbols.contains(lastContent))return true;
        if((!mathSymbols.contains(lastContent)&&mathSymbols.contains(content))||
                (mathSymbols.contains(lastContent)&&!mathSymbols.contains(content)))return true;
        else return false;
    }
    public void addNewContent(String content) {
        mathContents.add(content);
        if(mathContents.size()>7){
            startContentIndex=mathContents.size()-8;
            nextContent();
        }else updateMathBar();
        PluginCoder.getCoderGUI().getGuiPlayer().updateInventory();
    }
    public List<String> getMathContents() {
        return mathContents;
    }

    public int getStartContentIndex() {
        return startContentIndex;
    }

    public List<Boolean> getIsNewNumber() {
        return isNewNumber;
    }

    public List<Integer> getLastIndex() {
        return lastIndex;
    }

}