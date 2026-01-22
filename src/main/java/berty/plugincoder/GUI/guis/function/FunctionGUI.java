package berty.plugincoder.GUI.guis.function;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.GUI.InicVars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionGUI {
    private PluginCoder mainPlugin;
    private List<String> functions=new ArrayList<>();

    private List<Integer> functionsIndexes=new ArrayList<>();
    private int lastPageOpened=0;
    private List<String> initialFunctionContainer=new ArrayList<>();
    private List<Inventory> functionGUI=new ArrayList<>();
    private boolean deleteInstruction=false;
    private int moveInstructionIndex=-2;
    private int copyInstructionIndex=-2;
    private String lastFunctionChanges ="";
    private Inventory previousInventory;
    private int originalFunctionIndex=0;
    private boolean isRunningCode=false;
    public List<Inventory> getGUI() {
        return functionGUI;
    }
    public FunctionGUI(PluginCoder plugin){
        this.mainPlugin=plugin;
    }
    public void updateGUIWithNewFunction(int index, List<String> functionContainer){
        if(!lastFunctionChanges.isEmpty())saveChanges(false);
        functionGUI.clear();
        initialFunctionContainer=functionContainer;
        //para que las variables se actualicen solo la primera vez que se carguen
        PluginCoder.getCoderGUI().getExecutionWriterGUI().setLastFunctionInstructionVars(-1);
        List<String> containerClone= functions.size()>0?mainPlugin.getCodeExecuter()
                .getGUIInstructionsFromFunction(functions.get(functions.size()-1)):new ArrayList<>(functionContainer);
        String function=containerClone.get(index);
        if(functions.size()==0)originalFunctionIndex=index;
        functions.add(function);
        List<String> instructions=mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function);
        String functionName=function.replaceAll("^([^{]+)?\\{(.*)\\}$","$1");
        updateFunctionGUI(functionName,instructions);
    }
    public void updateGUIWithNewFunction(String function, List<String> functionContainer){
        if(!lastFunctionChanges.isEmpty())saveChanges(false);
        functionGUI.clear();
        initialFunctionContainer=functionContainer;
        String functionName=function.replaceAll("^([^{]+)\\{(.*)\\}$","$1");
        functions.add(function);
        List<String> instructions=mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function);
        if(mainPlugin.getSelectedPlugin().getActivationContainer().get(0).equals(function)){
           functionName=PluginCoder.getCoderGUI().getGuiText("onEnableTitle");
        }else if(mainPlugin.getSelectedPlugin().getDeactivationContainer().get(0).equals(function)){
            functionName=PluginCoder.getCoderGUI().getGuiText("onDisableTitle");
        }
        updateFunctionGUI(functionName,instructions);
    }
    public void updateGUIWithPreviousFunction(String function){
        functionGUI.clear();
        String functionName=function.replaceAll("^([^{]+)\\{(.*)\\}$","$1");
        List<String> instructions=mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function);
        if(mainPlugin.getSelectedPlugin().getActivationContainer().get(0).equals(function)){
            functionName=PluginCoder.getCoderGUI().getGuiText("onEnableTitle");
        }else if(mainPlugin.getSelectedPlugin().getDeactivationContainer().get(0).equals(function)){
            functionName=PluginCoder.getCoderGUI().getGuiText("onDisableTitle");
        }
        updateFunctionGUI(functionName,instructions);
    }
    private void updateFunctionGUI(String functionName,List<String> instructions) {
        PluginCoder.getCoderGUI().getExecutionWriterGUI().setLastFunctionInstructionVars(-1);
        if(instructions.isEmpty()){
            createPage(0,functionName);return;
        }
        int i=10;
        int invIndex=0;
        for(String instruction:instructions){
           instruction=PluginCoder.getCoderGUI().putTextColor(instruction);
            ItemStack instructionItem=getInstructionItem(instruction);
            if(i==10)createPage(invIndex,functionName);
            functionGUI.get(invIndex).setItem(i,instructionItem);
            if((i+2)%9==0){
                if(i+2==functionGUI.get(invIndex).getSize()-9){
                    invIndex++;i=10;
                }else i+=3;
            }
            else i++;
        }
    }
    private void createPage(int invIndex,String functionName){
        ItemStack execute=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/fa8f6b131ef847d9160e516a6f44bfa932554d40c18a81796d766a5487b9f710");
        ItemMeta meta=execute.getItemMeta();
        String title=PluginCoder.getCoderGUI().getGuiText("executeFunction");
        meta.setDisplayName(ChatColor.GREEN+title);
        execute.setItemMeta(meta);
        ItemStack add=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.MAP));
        meta=add.getItemMeta();
        String addTitle=PluginCoder.getCoderGUI().getGuiText("addInstruction");
        meta.setDisplayName(ChatColor.YELLOW+addTitle);
        add.setItemMeta(meta);
        ItemStack delete=new ItemStack(deleteInstruction?Material.LAVA_BUCKET:Material.BUCKET);
        meta=delete.getItemMeta();
        String deleteTitle=PluginCoder.getCoderGUI().getGuiText("deleteInstruction");
        meta.setDisplayName(ChatColor.GRAY+deleteTitle);
        delete.setItemMeta(meta);
        ItemStack move=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.ENDER_PEARL));
        meta=move.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA+PluginCoder.getCoderGUI().getGuiText("moveInstruction"));
        move.setItemMeta(meta);
        ItemStack copy=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.FEATHER));
        meta=copy.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("copyInstruction"));
        copy.setItemMeta(meta);
        ItemStack parameters=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
        meta=parameters.getItemMeta();
        String parametersEditText = PluginCoder.getCoderGUI().getGuiText("clickEditParams");
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(functionName)));
        List<String> lore=new ArrayList<>();
        lore.add(ChatColor.YELLOW+parametersEditText);
        meta.setLore(lore);
        parameters.setItemMeta(meta);
        String functionNameTitle=functionName.replaceAll("^([^(]+)\\((.*)\\)$","$1").toUpperCase();
        functionGUI.add(Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',"&f&l"+functionNameTitle)));
        PluginCoder.getCoderGUI().createInventoryBase(functionGUI.get(invIndex),true);
        functionGUI.get(invIndex).setItem(26,add);
        functionGUI.get(invIndex).setItem(35,delete);
        functionGUI.get(invIndex).setItem(18,move);
        functionGUI.get(invIndex).setItem(27,copy);
        if(functions.size()==1) functionGUI.get(invIndex).setItem(49,execute);
        if(previousInventory.equals(PluginCoder.getCoderGUI().getObjectGUI().getGUI())||functionName.endsWith(")")) functionGUI.get(invIndex).setItem(4,parameters);
        if(functions.stream().anyMatch(f->f.matches("^\\s*for\\s*\\((.+)$")||f.matches("^\\s*while\\s*\\((.+)$"))){
            functionGUI.get(invIndex).setItem(47,getInstructionItem("stop"));
            functionGUI.get(invIndex).setItem(51,getInstructionItem("continue"));
        } if(functions.stream().anyMatch(f->f.matches("^\\s*repeat\\s*\\((.+)$"))){
            functionGUI.get(invIndex).setItem(49,getInstructionItem("cancel"));
        }
    }
    public void saveChanges(boolean isReturning){
        if(functions.size()==0)return;
        String instructions="";
        int indexInstruction=0;
        List<String> instructionList=mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(lastFunctionChanges.isEmpty()?functions.get(functions.size()-1):lastFunctionChanges);
        lastFunctionChanges="";
        for(Inventory inv:functionGUI){
           for(int i=10;i<44;i++){
               if(i%9==0||(i+1)%9==0)continue;
               ItemStack itemStack=inv.getItem(i);
               if(itemStack==null||itemStack.getType()==Material.AIR)break;
               if(itemStack.getItemMeta().getLore()!=null) instructions+=instructionList.get(indexInstruction);
               else {
                   String instruction=ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
                   if(instruction.contains("if(")||instruction.startsWith("for(")||instruction.startsWith("while(")||
                           instruction.startsWith("delay(")||instruction.startsWith("repeat("))instruction+="{}";

                   instructions+=instruction+(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)?"":";");
               }
               indexInstruction++;
           }
        }
        String oldFunction= functions.get(functions.size()-1);
        String oldFunctionName=oldFunction.replaceAll("^([^{]+)\\{(.*)}$","$1");
        String newFunction=oldFunctionName+"{"+instructions+"}";
        ItemStack functionItem=functionGUI.get(0).getItem(4);
        if(!functionItem.getType().toString().contains("GLASS_PANE")){
            String newParams=ChatColor.stripColor(functionItem.getItemMeta().getDisplayName()).trim();
            newFunction=newFunction.replaceAll("^([^{]+)\\{(.*)}$",newParams+"{$2}");
        }
        //si no hay cambios, no guarda
        if(newFunction.equals(oldFunction)){
            if(isReturning){functions.remove(functions.size()-1);
            if(functionsIndexes.size()>0)functionsIndexes.remove(functionsIndexes.size()-1);
            }
            return;
        }
        functions.set(functions.size()-1,newFunction);
        for(int index=functions.size()-2;index>=0;index--){
            String newSubFunction=functions.get(index+1);
            String functionToUpdate=functions.get(index);
            String updatedFunction=functionToUpdate.replaceAll("^([^{]+)\\{(.*)}$","$1")+"{";
            int functionIndex=functionsIndexes.get(index);
            int instructionIndex=0;
            for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(functionToUpdate)){
                if(instructionIndex==functionIndex)updatedFunction+=newSubFunction;
                else updatedFunction+=instruction+(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)?"":";");
                instructionIndex++;
            }
            updatedFunction+="}";
            functions.set(index,updatedFunction);
        }
        initialFunctionContainer.set(originalFunctionIndex,functions.get(0));
        //if(initialFunctionContainer.equals(mainPlugin.getSelectedPlugin().getListener()))PluginCoder.getCoderGUI().getEventsGUI().updateLastEventEdited();
        if(isReturning){
            functions.remove(functions.size()-1);
            if(functionsIndexes.size()>0)functionsIndexes.remove(functionsIndexes.size()-1);
        }
        updateGUIFunctions();
    }
    public void deleteInstruction(int index){
        String function= lastFunctionChanges.isEmpty()?functions.get(functions.size()-1): lastFunctionChanges;
        lastFunctionChanges =function.replaceAll("^([^{]+)\\{(.*)}$","$1{");
        int instructionIndex=0;
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            if(index!=instructionIndex) lastFunctionChanges +=instruction+(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)?"":";");
            instructionIndex++;
        }
        lastFunctionChanges +="}";
    }
    public boolean addInstruction(String instruction) {
        if(getLastInstruction().startsWith("return ")){
            PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+"[Error] You cannot put an instruction after a return");//TODO traducir
            PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
            return false;
        }
        String functionName=functions.get(functions.size()-1).replaceAll("^([^{]+)\\{(.*)\\}$","$1").replaceAll("^(.+)->$","$1");
        ItemStack instructionItem=getInstructionItem(instruction);
        Inventory gui=functionGUI.get(functionGUI.size()-1);
        if(gui.getItem(43)!=null){
            int index=functions.size();
            createPage(index,functionName);
        }
        lastPageOpened=functionGUI.size()-1;
        Inventory lastGui=functionGUI.get(lastPageOpened);
        for(int i=10;i<44;i++){
            if(i%9==0||(i+1)%9==0)continue;
            if(lastGui.getItem(i)==null){
                lastGui.setItem(i,instructionItem);break;
            }
        }
        return true;
    }
    public ItemStack getInstructionItem(String instruction){
        ItemStack instructionItem;
        if(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)){
            instruction=ChatColor.stripColor(instruction.replace("&","§"));
            String instructionName=ChatColor.DARK_RED+instruction.replaceAll("^([^{]+)\\{(.*)}$","$1");
            instructionItem=PluginCoder.getCoderGUI().getFunctionItem(instruction,new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK)),instructionName);
        }else {
            instructionItem=!instruction.trim().isEmpty()?(new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP)))
                    :(new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.MAP)));
            instruction=ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(instruction));
            ItemMeta meta=instructionItem.getItemMeta();
            meta.setDisplayName(instruction);
            instructionItem.setItemMeta(meta);
        }
        return instructionItem;
    }

    public void updateFunctionIndexes(Inventory inventory,int slot) {
        functionsIndexes.add(functionGUI.indexOf(inventory)*28+slot-(10+2*(slot/9-1)));
    }

    public int[] getInstructionIndex() {
        int instructionSelectedIndex=functionsIndexes.get(functionsIndexes.size()-1);
        int functionPageIndex=instructionSelectedIndex/28;
        int normalizedIndex=instructionSelectedIndex%28;
        normalizedIndex=normalizedIndex+(10+2*(normalizedIndex/7));
        return new int[]{functionPageIndex,normalizedIndex};
    }

    public String getLastInstruction() {
        Inventory inventory=functionGUI.get(functionGUI.size()-1);
        boolean found=false;
        String instruction="";
        for(int i=10;i<44;i++){
            if(i%9==0||(i+1)%9==0)continue;
            ItemStack item=inventory.getItem(i);
            if(item!=null)instruction=ChatColor.stripColor(item.getItemMeta().getDisplayName());
            else{
                found=true;
                break;
            }
        }
        if(!found)instruction=ChatColor.stripColor(inventory.getItem(43).getItemMeta().getDisplayName());
        return instruction;
    }
    public void executeCode(Player p) {
        saveChanges(false);
        String function=functions.get(0);
        Map<String,Object> variables= InicVars.getInicVars(mainPlugin.getSelectedPlugin(),function);
        ErrorManager.setSender(p);
        mainPlugin.getCodeExecuter().executeFunction(function,"",variables);
        ErrorManager.setSender(Bukkit.getConsoleSender());
        if(!mainPlugin.getRepeat().getTaskIDs().isEmpty())isRunningCode=true;
        if(!isRunningCode)return;
        ItemStack stopExecution=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/cecd041f628c005a690fc6b8237e7311bb7c3b3aac10539fefe396a4c7c783e7");
        ItemMeta meta=stopExecution.getItemMeta();
        String title=PluginCoder.getCoderGUI().getGuiText("stopFunctionExecution");
        meta.setDisplayName(ChatColor.RED+title);
        stopExecution.setItemMeta(meta);
        for(Inventory inv:functionGUI){
            inv.setItem(49,stopExecution);
        }
    }
    public void stopCodeExecution() {
        isRunningCode=false;
        if(PluginCoder.getCoderGUI().getGuiPlayer()==null)return;
        for(int taskID:mainPlugin.getRepeat().getTaskIDs()){
            Bukkit.getScheduler().cancelTask(taskID);
        }
        mainPlugin.getRepeat().getTaskIDs().clear();
        ItemStack execute=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/fa8f6b131ef847d9160e516a6f44bfa932554d40c18a81796d766a5487b9f710");
        ItemMeta meta=execute.getItemMeta();
        String title=PluginCoder.getCoderGUI().getGuiText("executeFunction");
        meta.setDisplayName(ChatColor.GREEN+title);
        execute.setItemMeta(meta);
        for(Inventory inv:functionGUI){
            inv.setItem(49,execute);
        }
    }
    public int getNumOfInstructions() {
        int numInstructions=28*(functionGUI.size()-1);
        for(int slot=10;slot<44;slot++){
            if(slot%9==0||(slot+1)%9==0)continue;
            if(functionGUI.get(functionGUI.size()-1).getItem(slot)==null)break;
            numInstructions++;
        }
        return numInstructions;
    }
    public void saveToFunctionGUI(String instruction,boolean closingInventoryWithoutReturning){
        ItemStack functionSign=functionGUI.get(0).getItem(4).clone();
        ItemMeta meta=functionSign.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(instruction)));
        functionSign.setItemMeta(meta);
        for(int i=0;i<functionGUI.size();i++) functionGUI.get(i).setItem(4,functionSign);
        if(closingInventoryWithoutReturning){
            PluginCoder.getCoderGUI().getFunctionGUI().saveChanges(true);
            PluginCoder.getCoderGUI().returnHome(null,false);
        }
    }
    public void returnHome(Player p,boolean openInventory){
        moveInstructionIndex=-2;copyInstructionIndex=-2;deleteInstruction=false;
        saveChanges(false);
        PluginCoder.getCoderGUI().returnHome(p,openInventory);
        PluginCoder.getCoderGUI().getExecutionWriterGUI().setLastFunctionInstructionVars(-1);
    }
    public void returnPage(Player player) {
        ItemStack cursor=player.getItemOnCursor().clone();
        restoreInstructionMovement(player);
        moveInstructionIndex=-2;copyInstructionIndex=-2;deleteInstruction=false;
        if(functions.size()>1){
            int lastPage=functionsIndexes.get(functionsIndexes.size()-1)/28;
            saveChanges(true);
            updateGUIWithPreviousFunction(functions.get(functions.size()-1));
            if(player!=null)player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(lastPage));
        }else {
            saveChanges(true);
            openPreviousInv(player);
            PluginCoder.getCoderGUI().getExecutionWriterGUI().setLastFunctionInstructionVars(-1);
        }
        player.getInventory().remove(cursor);
    }

    private void updateGUIFunctions() {
        if(PluginCoder.getCoderGUI().getEventsGUI().getGUI().stream().anyMatch(inv->previousInventory.equals(inv))){
            PluginCoder.getCoderGUI().getEventsGUI().updateLastEventEdited();
        }else if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->previousInventory.equals(inv))){
            PluginCoder.getCoderGUI().getCommandsGUI().updateCommand();
        }else if(previousInventory.equals(PluginCoder.getCoderGUI().getObjectGUI().getGUI())){
            PluginCoder.getCoderGUI().getObjectGUI().updateSelectedFunction();
        }
    }
    private void openPreviousInv(Player p) {
        if(PluginCoder.getCoderGUI().getEventsGUI().getGUI().stream().anyMatch(inv->previousInventory.equals(inv))){
           p.openInventory(PluginCoder.getCoderGUI().getEventsGUI().getGUI().get(PluginCoder.getCoderGUI().getEventsGUI().getEventEditedIndex()/28));
        }else if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->previousInventory.equals(inv))){
             PluginCoder.getCoderGUI().getCommandsGUI().openCommandPage(p);
        }else if(previousInventory.equals(PluginCoder.getCoderGUI().getObjectGUI().getGUI())){
            p.openInventory(PluginCoder.getCoderGUI().getObjectGUI().getGUI());
        }else p.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
    }
    public void insertInstructionInFunction(int instructionIndex,int newIndex,boolean deleteOldIndex){
        if(instructionIndex==newIndex&&deleteOldIndex)return;
        String function= lastFunctionChanges.isEmpty()?functions.get(functions.size()-1):lastFunctionChanges;
        List<String> instructions=mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function);
        String newFunction=function.replaceAll("^([^{]+)\\{(.*)\\}$","$1{");
        String insertedInstruction=instructions.get(instructionIndex);
        if(deleteOldIndex)instructions.remove(instructionIndex);
        for(int index=0;index<instructions.size();index++){
            String instruction=instructions.get(index);
            if(index==newIndex)newFunction+=insertedInstruction+(mainPlugin.getCodeExecuter().instructionIsFunction(insertedInstruction)?"":";");
            newFunction+=instruction+(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)?"":";");
        }
        if(newIndex>=instructions.size())newFunction+=insertedInstruction+(mainPlugin.getCodeExecuter().instructionIsFunction(insertedInstruction)?"":";");
        newFunction+="}";
        lastFunctionChanges=newFunction;
    }
    public void insertInstructionItem(ItemStack instructionItem,int inventoryIndex,int instructionSlot){
        ItemStack lastInventoryHolder=functionGUI.get(functionGUI.size()-1).getItem(43);
        String functionName=functions.get(functions.size()-1).replaceAll("^([^{]+)?\\{(.*)\\}$","$1");
        if(lastInventoryHolder!=null&&lastInventoryHolder.getType()!=Material.AIR)createPage(functionGUI.size(),functionName);
        List<ItemStack> instructionItems=new ArrayList<>();
        ItemStack actualSlotItem=functionGUI.get(inventoryIndex).getItem(instructionSlot);
        if(actualSlotItem!=null&&actualSlotItem.getType()!=Material.AIR){
            for(int index=inventoryIndex;index<functionGUI.size();index++){
                int startingSlot=index==inventoryIndex?instructionSlot:10;
                for(int slot=startingSlot;slot<44;slot++){
                    if(slot%9==0||(slot+1)%9==0)continue;
                    ItemStack item=functionGUI.get(index).getItem(slot);
                    functionGUI.get(index).setItem(slot,null);
                    if(item==null||item.getType()==Material.AIR)break;
                    instructionItems.add(item.clone());
                }
            }
            int itemInstructionIndex=0;
            for(int index=inventoryIndex;index<functionGUI.size();index++){
                int startingSlot=index==inventoryIndex?instructionSlot+1:10;
                for(int slot=startingSlot;slot<44;slot++){
                    if(slot%9==0||(slot+1)%9==0)continue;
                    if(itemInstructionIndex==instructionItems.size())break;
                    functionGUI.get(index).setItem(slot,instructionItems.get(itemInstructionIndex));
                    itemInstructionIndex++;
                }
            }
        }else{
            int firstInvIndex=inventoryIndex;
            for(int index=inventoryIndex;index>=0;index--){
                for(int slot=firstInvIndex==index?instructionSlot-1:43;slot>=10;slot--){
                    if(slot%9==0||(slot+1)%9==0)continue;
                    ItemStack slotItem=functionGUI.get(index).getItem(slot);
                    if(slotItem!=null&&slotItem.getType()!=Material.AIR)break;
                    instructionSlot=slot;inventoryIndex=index;
                }
            }
        }
        functionGUI.get(inventoryIndex).setItem(instructionSlot,instructionItem);
    }
    public void restoreInstructionMovement(Player player){
        if(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()>-1){
            int instructionSlot=moveInstructionIndex%28;
            instructionSlot=instructionSlot+(10+2*(instructionSlot/7));
            insertInstructionItem(player.getItemOnCursor(),functionGUI.indexOf(player.getOpenInventory().getTopInventory()),instructionSlot);
            player.setItemOnCursor(null);
        }
    }
    public List<String> getFunctions() {
        return functions;
    }
    public List<Integer> getFunctionsIndexes() {
        return functionsIndexes;
    }
    public List<String> getInitialFunctionContainer() {
        return initialFunctionContainer;
    }

    public boolean isDeleteInstruction() {
        return deleteInstruction;
    }

    public void setDeleteInstruction(boolean deleteInstruction) {
        this.deleteInstruction = deleteInstruction;
    }

    public int getMoveInstructionIndex() {
        return moveInstructionIndex;
    }

    public void setMoveInstructionIndex(int moveInstruction) {
        this.moveInstructionIndex = moveInstruction;
    }

    public int getCopyInstructionIndex() {
        return copyInstructionIndex;
    }

    public void setCopyInstructionIndex(int copyInstruction) {
        this.copyInstructionIndex = copyInstruction;
    }

    public int getLastPageOpened() {
        return lastPageOpened;
    }

    public void setLastPageOpened(int lastPageOpened) {
        this.lastPageOpened = lastPageOpened;
    }

    public void setOriginalFunctionIndex(int originalFunctionIndex) {

        this.originalFunctionIndex = originalFunctionIndex;
    }

    public void setPreviousInventory(Inventory previousInventory) {
        this.previousInventory = previousInventory;
    }

    public boolean isRunningCode() {
        return isRunningCode;
    }

}
