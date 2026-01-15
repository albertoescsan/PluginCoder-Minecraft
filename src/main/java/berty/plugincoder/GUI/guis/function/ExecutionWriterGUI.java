package berty.plugincoder.GUI.guis.function;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.GUI.MethodDescriptions;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.GUI.InicVars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ExecutionWriterGUI {

    private PluginCoder plugin;
    private Inventory gui;
    private int lastFunctionInstructionVars=-1;
    private List<String> renderedInstructions=new ArrayList<>();
    private List<Integer> paramSlots=new ArrayList<>();
    private List<String> methodExecutedTypes=new ArrayList<>();
    private Map<String,String> variableTypes=new HashMap<>();
    private Map<String,List<String>> methodTypes=new HashMap<>();
    private List<Inventory>previousInvs=new ArrayList<>();
    private int page=0;
    public ExecutionWriterGUI(PluginCoder plugin) {
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    public Inventory getGui() {
        return gui;
    }
    public void createInventory(){
        gui= Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,true);
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
        gui.setItem(4,instructionItem);
        String deleteTitle=PluginCoder.getCoderGUI().getGuiText("deleteProperty");
        ItemStack delete=new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+deleteTitle);
        delete.setItemMeta(meta);
        gui.setItem(22,delete);
    }
    public void updateGUI(String instruction,Inventory inventory){
        previousInvs.add(inventory);
        renderedInstructions.add(instruction);
        updateGUI(instruction);
    }
    private void updateGUI(String instruction){
        for(int i=0;i<7;i++) gui.setItem(10+i,null);
        //actualizar variables si la instruccion es nueva
        updateVariables();methodExecutedTypes.clear();
        String[] methods=instruction.trim().isEmpty()?new String[]{}:plugin.getCodeExecuter().getMethodsOfInstruction(instruction);
        if(methods.length>0){
            ItemStack variable=new ItemStack(Material.CHEST);
            ItemMeta meta=variable.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+methods[0]);
            variable.setItemMeta(meta);
            gui.setItem(10,variable);
            //actualizar los tipos de variables y metodos
            String variableType=variableTypes.get(methods[0]);
            methodExecutedTypes.add(variableType);
            methodTypes=plugin.getCodeUtils().getMethodsOfType(variableType);
            for(int i=1;i<methods.length;i++){
                String method=methods[i].replaceAll("^([^(]+)\\((.*)\\)$","$1");//nombre del metodo
                if(method.length()!=methods[i].length())method+="()";
                String methodType=methodTypes.get(method).get(0);
                if(methodTypes.get(method).size()>1) methodType=getBestMethodType(methods[i],method,variableType);
                methodExecutedTypes.add(methodType);
                methodTypes=plugin.getCodeUtils().getMethodsOfType(methodType);
                variableType=methodType;
                boolean finishMethod=plugin.getCodeUtils().isFinishMethod(methodType);
                ItemStack methodItem=new ItemStack(finishMethod?Material.DISPENSER:Material.HOPPER);
                meta=methodItem.getItemMeta();
                meta.setDisplayName((finishMethod?ChatColor.GRAY:ChatColor.DARK_GRAY)+methods[i]);
                methodItem.setItemMeta(meta);
                gui.setItem(10+i,methodItem);
            }
            updateSelectBar(true);
        }else updateSelectBar(false);
        updateInstructionView();
    }

    private String getBestMethodType(String methodText,String method,String variableType) {
        try {
            variableType=plugin.getCodeUtils().clearContainerType(variableType);
            List<Object> paramTypes=new ArrayList<>();
            for(String param:plugin.getCodeExecuter().getStringParameters(methodText)){
                String paramType=plugin.getCodeUtils().getTypeOfExecution(plugin.getSelectedPlugin(),param,variableTypes);
                paramType=plugin.getCodeUtils().clearContainerType(paramType);
                paramTypes.add(paramType);
            }
            Method javaMethod=plugin.getCodeExecuter().getMethod(Class.forName(variableType),method,paramTypes,false);
            return javaMethod.getReturnType().getTypeName();
        }catch (Exception e){ return "";}
    }

    public String getTypeOfExecution(String execution){
        updateVariables();
        String type=plugin.getCodeUtils().getTypeOfExecution(plugin.getSelectedPlugin(),execution,variableTypes);
        return plugin.getCodeUtils().clearContainerType(type);
    }
    private void updateSelectBar(boolean renderMethods){
        //eliminar elementos anteriores
        for(int i=28;i<35;i++){
            gui.setItem(i,null);
            gui.setItem(i+9,null);
        }
        if(renderMethods&&methodTypes.isEmpty())return;
        //obtener y ordenar elementos
        List<String> elements;
        String classType=methodExecutedTypes.size()>0?methodExecutedTypes.get(methodExecutedTypes.size()-1):"";
        if(renderMethods)elements=new ArrayList<>(methodTypes.keySet());
        else elements=new ArrayList<>(variableTypes.keySet());
        elements=elements.stream().sorted().collect(Collectors.toList());
        //quedarme con los elementos a mostrar
        if(elements.size()>14*(page+1))elements=elements.subList(14*page,14*(page+1));
        else elements=elements.subList(14*page,elements.size());
        AtomicInteger addedSlot= new AtomicInteger();
        elements.stream().forEach(element->{
            String returnType;
            if(renderMethods){
                if(methodTypes.get(element).size()>1)returnType=methodTypes.get(element).stream().filter(type->!type.equals("void")).findFirst().orElse(String.class.getTypeName());
                else returnType=methodTypes.get(element).get(0);
            }else returnType=variableTypes.get(element);
            ItemStack method=getMethodItem(element,classType,returnType,renderMethods);
            gui.setItem(28+ addedSlot.get(),method);
            if(addedSlot.get() ==6) addedSlot.addAndGet(3);
            else addedSlot.getAndIncrement();
        });
    }

    private ItemStack getMethodItem(String element,String classType,String returnType,boolean renderMethods) {
        boolean finishMethod=plugin.getCodeUtils().isFinishMethod(returnType);
        ItemStack method=new ItemStack(renderMethods?(finishMethod?Material.DISPENSER:Material.HOPPER):Material.CHEST);
        ItemMeta meta=method.getItemMeta();
        meta.setDisplayName((renderMethods?(finishMethod?ChatColor.GRAY:ChatColor.DARK_GRAY):ChatColor.GOLD)+element);
        method.setItemMeta(meta);
        if(renderMethods)method=MethodDescriptions.setDescription(element,classType,method);
        return method;
    }

    private void updateInstructionView(){
        String execution="";
        for(int i=0;i<7;i++){
            ItemStack item=gui.getItem(10+i);
            if(item==null||item.getType()==Material.AIR)break;
            execution+=ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim()+".";
        }
        execution=PluginCoder.getCoderGUI().putTextColor(execution.isEmpty()?"":execution.substring(0,execution.length()-1));
        ItemMeta meta=gui.getItem(4).getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&f"+execution));
        gui.getItem(4).setItemMeta(meta);
    }
    public void updateVariables(){
        if(plugin.getFunctionGUI().getFunctionsIndexes().size()==0){
            variableTypes= InicVars.getInicVarTypes(plugin.getSelectedPlugin(),"");return;
        }
        int functionIndex=plugin.getFunctionGUI().getFunctionsIndexes().get(plugin.getFunctionGUI().getFunctionsIndexes().size()-1);
        if(functionIndex==lastFunctionInstructionVars)return;
        plugin.getFunctionGUI().saveChanges(false);
        lastFunctionInstructionVars=functionIndex;
        String function=plugin.getFunctionGUI().getFunctions().get(0);
        variableTypes=InicVars.getInicVarTypes(plugin.getSelectedPlugin(),function);
        Map<String,Map<String,List<String>>> metodosPorTipo=new HashMap<>();
        updateVariableTypes(function,0,metodosPorTipo);
    }
    private void updateVariableTypes(String function,int funtionIndex,Map<String,Map<String,List<String>>> metodosPorTipo){
        int executeInstructionIndex=0;
        if(plugin.getFunctionGUI().getFunctionsIndexes().size()==funtionIndex)return;
        int instructionToUpdateIndex=plugin.getFunctionGUI().getFunctionsIndexes().get(funtionIndex);
        for(String instruction:plugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            //es la instruccion y esta en la misma subfuncion
            if(plugin.getCodeExecuter().instructionIsFunction(instruction)){
                if(executeInstructionIndex!=instructionToUpdateIndex){
                    executeInstructionIndex++;
                    continue;
                }
                if(instruction.matches("^\\s*for\\s*\\(([^:]+)\\:(.+)\\)\\{(.*)\\}$")){
                    String forVar=instruction.replaceAll("^\\s*for\\s*\\(([^:]+)\\:(.+)\\)\\{(.*)\\}$","$1");
                    String iterator=instruction.replaceAll("^\\s*for\\s*\\(([^:]+)\\:(.+)\\)\\{(.*)\\}$","$2");
                    try{
                        Double.parseDouble(String.valueOf(iterator.charAt(0)));
                        variableTypes.put(forVar,double.class.getTypeName());
                    }catch (Exception e){
                        String iteratorType=plugin.getCodeUtils().getTypeOfExecution(plugin.getSelectedPlugin(),iterator,variableTypes).replaceAll("^([^.]+)\\.(.+)$","$2");
                        variableTypes.put(forVar,iteratorType);
                    }
                }
                updateVariableTypes(instruction,funtionIndex+1,metodosPorTipo);break;
            }else{
                if(executeInstructionIndex==instructionToUpdateIndex)break;
                if(instruction.matches("^([^=]+)\\=(.+)$")){
                    String varName=instruction.replaceAll("^([^=]+)\\=(.+)$","$1");
                    String execution=instruction.replaceAll("^([^=]+)\\=(.+)$","$2");
                    String[] methods=plugin.getCodeExecuter().getMethodsOfInstruction(execution);
                    String varType=variableTypes.get(methods[0]);
                    if(varType==null){
                        String constructorName=execution.replaceAll("^([a-zA-Z]+)\\((.+)$","$1");
                        Class objectClass=plugin.getConstructorTranslator().get(constructorName);
                        PluginObject pluginObject=plugin.getSelectedPlugin().getObject(constructorName);
                        if(pluginObject!=null)varType="PluginObject."+pluginObject.getName();
                        else if(objectClass!=null)varType=objectClass.getTypeName();
                        else varType=plugin.getCodeUtils().getTypeOfExecution(plugin.getSelectedPlugin(),execution,variableTypes);
                        /*
                        if(varType.equals("List.")||varType.equals("Set.")){
                            varType+=PredictType.predictTypeOfContainer(function,varName,varType.replaceAll("^([^.]+)\\.(.+)$","$1"),false,new ArrayList<>(variableTypes.keySet()));
                        }else if(varType.equals("Map.")){
                            String keyType=PredictType.predictTypeOfContainer(function,varName,"Map",true,new ArrayList<>(variableTypes.keySet()));
                            String valueType=PredictType.predictTypeOfContainer(function,varName,"Map",false,new ArrayList<>(variableTypes.keySet()));
                            varType+=keyType+"-"+valueType;
                        }*/
                    }else{
                        for(int i=1;i<methods.length;i++){
                            Map<String,List<String>> varExecuteMethods=metodosPorTipo.get(varType);
                            if(varExecuteMethods==null){
                                varExecuteMethods=plugin.getCodeUtils().getMethodsOfType(varType);
                                metodosPorTipo.put(varType,varExecuteMethods);
                            }
                            String method=methods[i];
                            if(method.endsWith(")")){
                                method=method.replaceAll("^([^(]+)\\((.+)\\)$","$1()");
                            }
                            if(varExecuteMethods.get(method).size()>1){
                                varType=getBestMethodType(methods[i],method,varType);
                            }else varType=varExecuteMethods.get(method).get(0);
                        }
                    }
                    variableTypes.put(varName,varType);
                }
            }
            executeInstructionIndex++;
        }
    }

    public void saveToExecutiongWritterGUI(String method,boolean closingInventoryWithoutReturning){
        String[] methods=plugin.getCodeExecuter().getMethodsOfInstruction(renderedInstructions.get(renderedInstructions.size()-1));
        int index=paramSlots.remove(paramSlots.size()-1)-10;
        String newInstruction=methods[0];
        for(int i=1;i<methods.length;i++){
            if(i==index){
                newInstruction+="."+method;
                String methodType=getTypeOfExecution(newInstruction);
                if(!methodType.equals(methodExecutedTypes.get(i))){
                    gui.setItem(10+i,getMethodItem(method,methodExecutedTypes.get(i-1),methodType,true));
                    break;
                }
            }else newInstruction+="."+methods[i];
        }
        renderedInstructions.set(renderedInstructions.size()-1,newInstruction);
        updateGUI(newInstruction);
        updateInstructionView();
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
    public boolean saveChanges(boolean closingInventoryWithoutReturning){
        Inventory previousInv=previousInvs.remove(previousInvs.size()-1);
        renderedInstructions.remove(renderedInstructions.size()-1);
        String newExecuteInstruction=getExecutedInstruction();
        if(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(previousInv))){
            int[] instructionPageSlot=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionIndex();
            //borrar index porque ya se ha guardado el cambio en functionGUI
            PluginCoder.getCoderGUI().getFunctionGUI().getFunctionsIndexes().remove(
                    PluginCoder.getCoderGUI().getFunctionGUI().getFunctionsIndexes().size()-1);
            ItemStack newInstructionItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(newExecuteInstruction);
            PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(instructionPageSlot[0]).setItem(instructionPageSlot[1],newInstructionItem);
            if(closingInventoryWithoutReturning)PluginCoder.getCoderGUI().getFunctionGUI().saveChanges(false);
        }
        else if(previousInv.equals(plugin.getSetValueGUI().getGUI())){
            PluginCoder.getCoderGUI().getSetValueGUI().saveToSetGuiPage(newExecuteInstruction,closingInventoryWithoutReturning);

        }else if(previousInv.equals(PluginCoder.getCoderGUI().getMathGUI().getGUI())){
            return PluginCoder.getCoderGUI().getMathGUI().saveToMathGUI(newExecuteInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getConditionsGUI().getGUI())){
            PluginCoder.getCoderGUI().getConditionsGUI().saveToConditionsGUI(newExecuteInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().getGUI())){
            PluginCoder.getCoderGUI().getCheckObjectTypeGUI().saveToCheckObjectTypeGUI(newExecuteInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI())){
            PluginCoder.getCoderGUI().getEqualityGUI().saveToEqualityGUI(newExecuteInstruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getTextGUI().getGUI())){
            PluginCoder.getCoderGUI().getTextGUI().saveToTextGUI(newExecuteInstruction,closingInventoryWithoutReturning,true);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI())){
            PluginCoder.getCoderGUI().getParametersGUI().saveToParametersGUI(newExecuteInstruction,closingInventoryWithoutReturning);
        }else{
            //TODO mas posibilidades
        }
        return true;
    }

    public String getExecutedInstruction() {
        String executeInstruction="";
        for(int i=10;i<17;i++){
            ItemStack methodItem=gui.getItem(i);
            if(methodItem==null)break;
            executeInstruction+=ChatColor.stripColor(methodItem.getItemMeta().getDisplayName())+".";
        }
        if(!executeInstruction.isEmpty())executeInstruction=executeInstruction.substring(0,executeInstruction.length()-1);
        return executeInstruction;
    }

    public void returnPage(Player p) {
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        saveChanges(false);
        methodExecutedTypes.clear();methodTypes.clear();
        p.openInventory(previousInv);
    }
    public void returnHome(Player p) {
        saveChanges(true);
        renderedInstructions.clear();lastFunctionInstructionVars=-1;previousInvs.clear();
        methodExecutedTypes.clear();methodTypes.clear();
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public boolean selectNewMethod(ItemStack methodItem) {
        if(methodExecutedTypes.size()==7)return false;
        page=0;
        String method=ChatColor.stripColor(methodItem.getItemMeta().getDisplayName());
        String methodReturnType;
        if(methodExecutedTypes.size()>0){
            if(methodTypes.get(method).size()>1){
                methodReturnType=getBestMethodType(method,method,methodExecutedTypes.get(methodExecutedTypes.size()-1));
            }else methodReturnType=methodTypes.get(method).get(0);
        }
        else methodReturnType=variableTypes.get(method);
        methodExecutedTypes.add(methodReturnType);
        methodTypes=plugin.getCodeUtils().getMethodsOfType(methodReturnType);
        updateSelectBar(true);
        for(int i=10;i<17;i++){
            if(gui.getItem(i)!=null)continue;
            ItemStack newMethodItem=new ItemStack(methodItem.getType());
            ItemMeta meta=newMethodItem.getItemMeta();
            meta.setDisplayName(methodItem.getItemMeta().getDisplayName());
            newMethodItem.setItemMeta(meta);gui.setItem(i,newMethodItem);break;
        }
        updateInstructionView();
        return true;
    }
    public boolean deleteMethod() {
        if(methodExecutedTypes.size()==0)return false;
        page=0;
        methodExecutedTypes.remove(methodExecutedTypes.size()-1);
        boolean deleted=false;
        for(int i=10;i<17;i++){
            if(gui.getItem(i)==null){
                gui.setItem(i-1,null);deleted=true;
                break;
            }
        }
        if(!deleted)gui.setItem(16,null);
        if(methodExecutedTypes.size()!=0){
            String methodReturnType=methodExecutedTypes.get(methodExecutedTypes.size()-1);
            methodTypes=plugin.getCodeUtils().getMethodsOfType(methodReturnType);
            updateSelectBar(true);
        } else {
            methodTypes.clear();
            updateSelectBar(false);
        }
        updateInstructionView();
        return true;
    }

    public boolean previousPage() {
        if(page==0)return false;
        page-=1;
        updateSelectBar(methodExecutedTypes.size()!=0);
        return true;
    }

    public boolean nextPage() {
        Map<String,List<String>> variableTypes=new HashMap<>();
        for(String var: variableTypes.keySet()){
            variableTypes.put(var,Arrays.asList(this.variableTypes.get(var)));
        }
        Map<String,List<String>> methods=methodExecutedTypes.size()!=0?methodTypes:variableTypes;
        if(methods.size()>(page+1)*14)page++;
        else return false;
        updateSelectBar(methodExecutedTypes.size()!=0);
        return true;
    }
    public void setLastFunctionInstructionVars(int lastFunctionInstructionVars) {
        this.lastFunctionInstructionVars = lastFunctionInstructionVars;
    }
    public Set<String> getVariables(){
        return variableTypes.keySet();
    }

    public List<String> getMethodExecutedTypes() {
        return methodExecutedTypes;
    }

    public void prepareToParametersGUI(int slot){
        paramSlots.add(slot);
        renderedInstructions.set(renderedInstructions.size()-1,getExecutedInstruction());
    }

    public Map<String, String> getVariableTypes() {
        return variableTypes;
    }
}
