package berty.plugincoder.GUI.guis.conditions;

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
import java.util.stream.Collectors;

public class ConditionsGUI {

    private PluginCoder mainPlugin;

    private List<String> renderedInstructions=new ArrayList<>();
    private List<Inventory> previousInvs=new ArrayList<>();
    private List<Boolean> isNewElement=new ArrayList<>();
    private List<String> conditionalContents=new ArrayList<>();
    private List<Integer> lastIndex=new ArrayList<>();
    private int startContentIndex=0;
    public ConditionsGUI(PluginCoder plugin){
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private Inventory gui;

    private void createInventory(){
        gui= Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,false);
        ItemStack and=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/4e41748121626f22ae16a4c664c7301a9f8ea591bf4d29888957682a9fdaf");
        ItemMeta meta=and.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"and");
        and.setItemMeta(meta);
        ItemStack or=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/cbb1d17cebc5f0ecc987b80efc03e32ecb1cb40dbc5bce2faf3e60542a40");
        meta=or.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"or");
        or.setItemMeta(meta);
        ItemStack exclamation=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d");
        meta=exclamation.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"!");
        exclamation.setItemMeta(meta);
        ItemStack openParentesis=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/674153bb6a3fa8979a889f99aa0fc84ba9f427ff0d8e487f76fa6c8831d18e9");
        meta=openParentesis.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"(");
        openParentesis.setItemMeta(meta);
        ItemStack closeParentesis=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/9c60e240cda0d9bb315b385119e5a9482e885827baf018e1c03b6a6d612d3f");
        meta=closeParentesis.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+")");
        closeParentesis.setItemMeta(meta);
        gui.setItem(37,and);gui.setItem(38,or);gui.setItem(39,exclamation);gui.setItem(40,openParentesis);gui.setItem(41,closeParentesis);
        ItemStack negro= mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        gui.setItem(28,negro); gui.setItem(30,negro); gui.setItem(32,negro); gui.setItem(34,negro);
        ItemStack backItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5");
        meta=backItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        backItem.setItemMeta(meta);
        ItemStack nextItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311");
        meta=nextItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        nextItem.setItemMeta(meta);
        gui.setItem(18,backItem);gui.setItem(26,nextItem);
        ItemStack instructionItem=new ItemStack(mainPlugin.getVersionNumber()<14?Material.getMaterial("SIGN"):Material.OAK_SIGN);
        gui.setItem(4,instructionItem);
        ItemStack trueItem= mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("WOOL"),1,(short)5):new ItemStack(Material.LIME_WOOL);
        meta=trueItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"true");
        trueItem.setItemMeta(meta);
        ItemStack falseItem= mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("WOOL"),1,(short)14):new ItemStack(Material.RED_WOOL);
        meta=falseItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED+"false");
        falseItem.setItemMeta(meta);
        gui.setItem(42,trueItem);gui.setItem(43,falseItem);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        ItemStack vars=new ItemStack(Material.CHEST);
        ItemMeta meta=vars.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("variablesTitle"));
        vars.setItemMeta(meta);
        gui.setItem(31,vars);
        ItemStack delete=new ItemStack(Material.LAVA_BUCKET);
        meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteLast"));
        delete.setItemMeta(meta);gui.setItem(22,delete);
        ItemStack equality=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/d773155306c9d2d58b149673951cbc6666aef87b8f873538fc85745f01b51");
        meta=equality.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("equality"));
        equality.setItemMeta(meta);
        ItemStack checkObjType=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/9c86ad9eb90f3552a0f710e300002f181086590799a7bc20ab37c6ae2ce903e0");
        meta=checkObjType.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("checkObjectType"));
        checkObjType.setItemMeta(meta);
        gui.setItem(29,equality);gui.setItem(33,checkObjType);
    }
    public void updateInventory(String instruction,Inventory inventory){
        renderedInstructions.add(instruction);
        previousInvs.add(inventory);
        updateInventory(instruction);
    }
    private void updateInventoryWithPreviousInstruction(){
        updateInventory(renderedInstructions.get(renderedInstructions.size()-1));
    }
    private void updateInventory(String instruction){
        conditionalContents.clear();
        updateConditionsContents(instruction);
        startContentIndex=0;
        updateConditionsBar();
    }
    private void updateConditionsContents(String instruction) {
        conditionalContents.clear();
        if(instruction.equals("true")){conditionalContents.add("true");return;}
        if(instruction.equals("false")){conditionalContents.add("false");return;}
        fillConditionalContents(instruction);
    }
    private void fillConditionalContents(String instruction){
        String[] booleans= mainPlugin.getCodeExecuter().getElementsNotModifyingParentesis(instruction, new String[]{" and ", " or "});
        List<String> operadores= Arrays.stream(mainPlugin.getCodeExecuter().getElementsNotModifyingParentesis(instruction, new String[]{" "}))
                .filter(op->op.equals("and")||op.equals("or")).collect(Collectors.toList());
        for(int i=0;i<booleans.length;i++){
            String booleanInst=booleans[i];
            if(!(booleanInst.startsWith("(")&&booleanInst.endsWith(")")))conditionalContents.add(booleanInst);
            else {
                conditionalContents.add("(");
                fillConditionalContents(booleanInst.substring(1,booleanInst.length()-1));
                conditionalContents.add(")");
            }
            if(i<operadores.size())conditionalContents.add(operadores.get(i));
        }
    }
    public void returnPage(Player p) {
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        saveChanges(false);
        p.openInventory(previousInv);
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
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI())){
            PluginCoder.getCoderGUI().getParametersGUI().saveToParametersGUI(newInstruction,closingInventoryWithoutReturning);
        }else{
            //TODO añadir mas inventarios
        }
        return true;
    }
    public void prepareToNextGUI(){
        renderedInstructions.set(renderedInstructions.size()-1,ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim());
    }
    public void returnHome(Player p) {
        saveChanges(true);
        lastIndex.clear();isNewElement.clear();
        conditionalContents.clear();renderedInstructions.clear();previousInvs.clear();
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public Inventory getGUI() {
        return gui;
    }

    public List<String> getConditionalContents() {
        return conditionalContents;
    }

    public int getStartContentIndex() {
        return startContentIndex;
    }
    public void updateConditionsBar() {
        PluginCoder.getCoderGUI().updateLinedElementsGUI(gui,conditionalContents,startContentIndex,
                content->getContentItem((String)content));
    }

    private ItemStack getContentItem(String operation) {
        ItemStack operationItem;
        if(operation.equals("and"))operationItem=gui.getItem(37);
        else if(operation.equals("or"))operationItem=gui.getItem(38);
        else if(operation.equals("!"))operationItem=gui.getItem(39);
        else if(operation.equals("("))operationItem=gui.getItem(40);
        else if(operation.equals(")"))operationItem=gui.getItem(41);
        else if(operation.equals("true"))operationItem=gui.getItem(42);
        else if(operation.equals("false"))operationItem=gui.getItem(43);
        else{
            operationItem=!operation.isEmpty()?(new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP)))
                    :(new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.MAP)));
            ItemMeta meta=operationItem.getItemMeta();
            meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(operation));
            operationItem.setItemMeta(meta);
            return operationItem;
        }
        ItemMeta meta=operationItem.getItemMeta();
        meta.setLore(new ArrayList<>());
        operationItem.setItemMeta(meta);
        return operationItem;
    }
    public boolean checkNewContent(String content){
        List<String> conditionalSymbols= Arrays.asList("and","or","!");
        if(conditionalContents.isEmpty())return true;
        String lastContent=conditionalContents.get(conditionalContents.size()-1);
        if(conditionalSymbols.contains(lastContent)&&content.equals("!"))return true;
        if(lastContent.equals("(")&&!conditionalSymbols.contains(content))return true;
        if(content.equals(")")&&!conditionalSymbols.contains(lastContent))return true;
        if((!conditionalSymbols.contains(lastContent)&&conditionalSymbols.contains(content))||
                (conditionalSymbols.contains(lastContent)&&!conditionalSymbols.contains(content)))return true;
        else return false;
    }
    public boolean nextContent(){
        if(startContentIndex+7<conditionalContents.size())startContentIndex++;
        else return false;
        updateConditionsBar();
        return true;
    }

    public boolean previousContent(){
        if(startContentIndex>0)startContentIndex--;
        else return false;
        updateConditionsBar();
        return true;
    }
    public boolean executionIsBoolean(String instruction){
        if(instruction.trim().isEmpty())return true;
        for(String execution: mainPlugin.getCodeExecuter().getElements(instruction,new String[]{" and "," or ","!","(",")"})){
            String booleanType=getBooleanType(execution);
            if(booleanType.equals("checkObjectType")||booleanType.equals("equality"))continue;
            String executionType= PluginCoder.getCoderGUI().getExecutionWriterGUI().getTypeOfExecution(execution);
            if(executionType==null||!executionType.equals(boolean.class.getTypeName()))return false;
        }
        return true;
    }
    public boolean saveToConditionsGUI(String newInstruction,boolean closingInventoryWithoutReturning){
        updateInventoryWithPreviousInstruction();
        if(!executionIsBoolean(newInstruction)){
            ErrorManager.notConditionalSequence();
            return false;
        }
        if(isNewElement.remove(isNewElement.size()-1)){
            if(checkNewContent(newInstruction))addNewContent(newInstruction);
            else {
                ErrorManager.consecutiveLogicSymbols();
                return false;
            }
        }else{
            conditionalContents.set(lastIndex.remove(lastIndex.size()-1),newInstruction);
        }
        updateConditionsBar();
        if(closingInventoryWithoutReturning)saveChanges(true);
        return true;
    }
    public void addNewContent(String content){
        conditionalContents.add(content);
        if(conditionalContents.size()>7){
            startContentIndex=conditionalContents.size()-8;
            nextContent();
        }else updateConditionsBar();
        PluginCoder.getCoderGUI().getGuiPlayer().updateInventory();
    }
    public String getBooleanType(String instruction){
        String cleanedInstruction="";
        int parentesisCount=0;
        for(Character c: instruction.toCharArray()){
            if(c.equals('('))parentesisCount++;
            else if(c.equals(')'))parentesisCount--;
            else if(parentesisCount==0)cleanedInstruction+=c;
        }
        if(cleanedInstruction.matches("^(.*) is (.*)$"))return "checkObjectType";
        else if(cleanedInstruction.matches("^(.*)=(.*)$")||cleanedInstruction.matches("^(.*)<(.*)$")
                ||cleanedInstruction.matches("^(.*)>(.*)$"))return "equality";
        else return "execution";
    }
    public List<Boolean> getIsNewElement() {
        return isNewElement;
    }
    public List<Integer> getLastIndex() {
        return lastIndex;
    }
}
