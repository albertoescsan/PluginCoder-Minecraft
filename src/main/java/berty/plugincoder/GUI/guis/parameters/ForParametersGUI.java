package berty.plugincoder.GUI.guis.parameters;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ForParametersGUI {

    private PluginCoder mainPlugin;
    private Inventory gui;
    private int selectedSlot;
    private boolean iterableVarSelected;
    private int iterablePage;
    private boolean isEditingVar=false;
    private List<ItemStack> iterableItems=new ArrayList<>();
    private String oldIteratorName;
    public ForParametersGUI(PluginCoder plugin){
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private void createInventory(){
        gui=Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,false);
        ItemStack instructionItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        gui.setItem(4,instructionItem);
        ItemStack negro= mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        for(int i=0;i<=3;i++)gui.setItem(10+i*2,negro);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
    }
    public void updateGUI(String instruction) {
        selectedSlot=0;iterablePage=0;iterableVarSelected=false;
        for(int i=0;i<7;i++){
            gui.setItem(28+i,null);
            gui.setItem(37+i,null);
        }
        PluginCoder.getCoderGUI().getExecutionWriterGUI().updateVariables();
        String[] params=instruction.replaceAll("^for\\s*\\((.*)\\)$","$1").split(":");
        ItemStack varItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("MAP"));
        ItemMeta meta=varItem.getItemMeta();
        if(params.length>=1&&!params[0].isEmpty()){
            meta.setDisplayName(ChatColor.WHITE+params[0]);
            varItem.setType(mainPlugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
            oldIteratorName=params[0];
        }else{
            meta.setDisplayName(ChatColor.WHITE+"");
            oldIteratorName="";
        }
        varItem.setItemMeta(meta);
        gui.setItem(11,varItem);
        ItemStack iterableItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("MAP"));
        meta=iterableItem.getItemMeta();
        if(params.length>=2){
            meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(params[1]));
            iterableItem.setType(mainPlugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
            if(params[1].contains("->")||params[1].contains("<-"))iterableVarSelected=true;
        }else meta.setDisplayName(ChatColor.WHITE+"");
        iterableItem.setItemMeta(meta);
        gui.setItem(13,iterableItem);
        ItemStack iteratorItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("MAP"));
        meta=iteratorItem.getItemMeta();
        if(params.length>=3){
            meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(params[2]));
            iterableItem.setType(mainPlugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
        }else{
            meta.setDisplayName(ChatColor.WHITE+"");
        }
        renderIterable();
        iteratorItem.setItemMeta(meta);
        gui.setItem(15,iteratorItem);
        updateInstructionSign();
    }
    public void saveToForParametersGUI(String instruction,boolean closingInventoryWithoutReturning) {
        ItemStack instructionItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("MAP"));
        ItemMeta meta=instructionItem.getItemMeta();
        if(!instruction.isEmpty()){
            meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(instruction));
            instructionItem.setType(mainPlugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
        }else meta.setDisplayName(ChatColor.WHITE+"");
        instructionItem.setItemMeta(meta);
        gui.setItem(selectedSlot,instructionItem);
        if(selectedSlot==29||selectedSlot==33){
            String arrow=ChatColor.stripColor(gui.getItem(31).getItemMeta().getDisplayName());
            String[] rangeNums=ChatColor.stripColor(gui.getItem(13).getItemMeta().getDisplayName()).trim().split(arrow);
            rangeNums[(selectedSlot-29)/4]=instruction;
            ItemStack iterableItem=gui.getItem(13);
            meta=iterableItem.getItemMeta();
            meta.setDisplayName(PluginCoder.getCoderGUI().putTextColor(rangeNums[0]+arrow+rangeNums[1]));
            iterableItem.setItemMeta(meta);
        }else if(selectedSlot==11){
            List<String> functions=PluginCoder.getCoderGUI().getFunctionGUI().getFunctions();
            String function=functions.get(functions.size()-1);
            String newFunction=PluginCoder.getCoderGUI().getVariableGUI().replaceFunctionWithNewVar(function,oldIteratorName,instruction);
            oldIteratorName=instruction;
            PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithPreviousFunction(newFunction);
        }
        updateInstructionSign();
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
    public void saveChanges(boolean closingInventoryWithoutReturning) {
        String instruction=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        PluginCoder.getCoderGUI().getFunctionGUI().saveToFunctionGUI(instruction,closingInventoryWithoutReturning);
    }

    public void returnPage(Player p) {
        saveChanges(false);
       p.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get( PluginCoder.getCoderGUI().getFunctionGUI().getLastPageOpened()));
    }

    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public void updateInstructionSign(){
       String varName=ChatColor.stripColor(gui.getItem(11).getItemMeta().getDisplayName()).trim();
       String iterable=gui.getItem(13).getItemMeta().getDisplayName();
       String iterator=ChatColor.stripColor(gui.getItem(15).getItemMeta().getDisplayName()).trim();
       ItemStack sign=gui.getItem(4);
       ItemMeta meta=sign.getItemMeta();
       if(iterator.isEmpty()||ChatColor.stripColor(iterator).equals("1")){
           meta.setDisplayName(ChatColor.WHITE+"for("+varName+":"+iterable+")");
       }else{
           iterator="§f"+PluginCoder.getCoderGUI().putTextColor(iterator);
           meta.setDisplayName(ChatColor.WHITE+"for("+varName+":"+iterable+":"+iterator+")");
       }
       sign.setItemMeta(meta);
    }
    public void renderIterable(){
        for(int i=0;i<7;i++){
            gui.setItem(28+i,null);
            gui.setItem(37+i,null);
        }
       if(iterableVarSelected){
           iterablePage=0;
           gui.setItem(45,gui.getItem(46));
           gui.setItem(53,gui.getItem(46));
           ItemStack negro= mainPlugin.getVersionNumber()<13?
                   new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
           ItemMeta meta=negro.getItemMeta();
           meta.setDisplayName(ChatColor.WHITE+"");
           negro.setItemMeta(meta);
           for(int i=0;i<7;i++){
               if(i%2==0)gui.setItem(28+i,negro);
               gui.setItem(37+i,negro);
           }
           iterableItems.clear();
           String iterable=ChatColor.stripColor(gui.getItem(13).getItemMeta().getDisplayName()).trim();
           ItemStack arrow; String[] rangeNums=null;
           boolean rightArrow=true;
           if(iterable.contains("<-")){
               arrow=PluginCoder.getCoderGUI().getBackItem();
               rangeNums=iterable.split("<-");rightArrow=false;
           }else if(iterable.contains("->")){
               arrow=PluginCoder.getCoderGUI().getNextItem();
               rangeNums=iterable.split("->");
           }else {
               iterable="";arrow=PluginCoder.getCoderGUI().getNextItem();
           }
           for(int i=0;i<2;i++){
               ItemStack num=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
               meta=num.getItemMeta();
               if(!iterable.isEmpty()){
                   meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(rangeNums[i]));
               }else{
                   meta.setDisplayName(ChatColor.WHITE+"0");
               }
               num.setItemMeta(meta);
               gui.setItem(29+i*4,num);
           }
           meta=arrow.getItemMeta();
           meta.setDisplayName(ChatColor.WHITE+(rightArrow?"->":"<-"));
           List<String> lore=new ArrayList<>();
           lore.add(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("clickChangeTo")
                   .replace("changeName",(!rightArrow?"->":"<-")));
           meta.setLore(lore);
           arrow.setItemMeta(meta);
           gui.setItem(31,arrow);
           iterableVarSelected=false;
           if(!iterable.isEmpty())return;
           ItemStack iterableItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem("0->0");
           gui.setItem(13,iterableItem);
           updateInstructionSign();
           return;
       }
        gui.setItem(45,PluginCoder.getCoderGUI().getBackItem());
        gui.setItem(53,PluginCoder.getCoderGUI().getNextItem());
        Map<String,List<String>> reverseTranslation= mainPlugin.getCodeUtils().getReverseTranslation();
        Set<String> iterables=new HashSet<>();
        for(String variable: PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariables()){
            String varType= mainPlugin.getCodeUtils().clearContainerType(PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariableTypes().get(variable));
            try{
                Class varClass=Class.forName(varType);
                if(!(Iterable.class.isAssignableFrom(varClass)||varClass.isArray())){
                    searchIterableVars(variable,varType,0,iterables,reverseTranslation);
                }else iterables.add(variable);
            }catch (Exception e){}
        }
        iterableVarSelected=true;
        List<ItemStack> iterableItems=new ArrayList<>();
        for(String iterable:iterables.stream().sorted().collect(Collectors.toList())){
            ItemStack iterableItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
            ItemMeta meta=iterableItem.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+iterable);
            iterableItem.setItemMeta(meta);
            iterableItems.add(iterableItem);
        }
        this.iterableItems=iterableItems;
        displayIterables();
    }

    private void displayIterables() {
        int index=0;
        for(int i=0;i<7;i++){
            gui.setItem(28+i,null);
            gui.setItem(37+i,null);
        }
        List<ItemStack> pageElements;
        if(iterableItems.size()>14*(iterablePage+1))pageElements=iterableItems.subList(14*iterablePage,14*(iterablePage+1));
        else pageElements=iterableItems.subList(14*iterablePage,iterableItems.size());
        for(ItemStack element:pageElements){
            gui.setItem(28+index,element);
            if(index==6)index+=3;
            else index++;
        }
    }

    private void searchIterableVars(String variable, String varType, int iterations, Set<String> iterables,
                                    Map<String,List<String>> reverseTranslation) throws ClassNotFoundException {
        if(iterations==2)return;
        if(mainPlugin.getCodeUtils().isFinishMethod(varType))return;
        Arrays.stream(Class.forName(varType).getMethods()).forEach(method -> {
            if(reverseTranslation.get(method.getName())==null)return;
            String methodName= mainPlugin.getCodeUtils().getMethodName(method,reverseTranslation.get(method.getName()));
            if(methodName==null)return;
            if(varType.equals(method.getReturnType().getTypeName()))return;
            //TODO cambiar y poner el tipo del parámetro
            String newVar=variable+"."+methodName+(method.getParameters().length>0&&!methodName.endsWith("()")?"()":"");
            String params="";
            for(Class param:method.getParameterTypes()){
                params+=param.getName().replaceAll("^(.+)\\.([^.]+)$","$2")+",";
            }
            params=params.isEmpty()?"":params.substring(0,params.length()-1);
            newVar=newVar.replace("()","("+params+")");
            String newVarType=method.getReturnType().getTypeName();
            try {
                Class returnClass=Class.forName(newVarType);
                if(!(Iterable.class.isAssignableFrom(returnClass)||returnClass.isArray())){
                    searchIterableVars(newVar,newVarType,iterations+1,iterables,reverseTranslation);
                }else iterables.add(newVar);
            }catch (Exception e){}
        });
    }
    public void changeArrow(){
        String arrow=ChatColor.stripColor(gui.getItem(31).getItemMeta().getDisplayName());
        ItemStack arrowItem;
        if(arrow.equals("->"))arrowItem=PluginCoder.getCoderGUI().getBackItem();
        else arrowItem=PluginCoder.getCoderGUI().getNextItem();

        ItemMeta meta=arrowItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+(!arrow.equals("->")?"->":"<-"));
        List<String> lore=new ArrayList<>();
        lore.add(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("clickChangeTo")
                .replace("changeName",(arrow.equals("->")?"->":"<-")));
        meta.setLore(lore);
        arrowItem.setItemMeta(meta);
        gui.setItem(31,arrowItem);
        ItemStack iterableItem=gui.getItem(13);
        meta=iterableItem.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replace(arrow,!arrow.equals("->")?"->":"<-"));
        iterableItem.setItemMeta(meta);
        updateInstructionSign();
    }
    public boolean previousPage(){
        if(iterablePage>0)iterablePage--;
        else return false;
        renderIterable();
        return true;
    }
    public boolean nextPage(){
        if(iterableItems.size()>(iterablePage+1)*14)iterablePage++;
        else return false;
        renderIterable();
        return true;
    }
    public void loadMathGUI(InventoryClickEvent event,Player p){
        String num=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
        selectedSlot=event.getSlot();
        PluginCoder.getCoderGUI().getMathGUI().updateGUI(num,event.getClickedInventory());
        p.openInventory(PluginCoder.getCoderGUI().getMathGUI().getGUI());
        PluginCoder.getCoderGUI().buttonSound(p);
    }
    public Inventory getGUI() {
        return gui;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public boolean isEditingVar() {
        return isEditingVar;
    }

    public void setEditingVar(boolean editingVar) {
        isEditingVar = editingVar;
    }
}
