package berty.plugincoder.GUI.guis.parameters;

import berty.plugincoder.GUI.InicVars;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.predictor.PredictType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ParametersGUI {

    private PluginCoder mainPlugin;
    private Inventory gui;
    private List<Inventory> previousInvs=new ArrayList<>();
    private List<String> paramTypes=new ArrayList<>();
    private List<ItemStack> renderElements=new ArrayList<>();
    private List<ContentParams> methodsOrConstructors=new ArrayList<>();
    private List<String> renderedMethods=new ArrayList<>();
    private List<String> classTypes=new ArrayList<>();
    private int elementPage=0;
    private List<Integer> paramSelectedIndexes=new ArrayList<>();

    private List<Integer> methodSelectedIndexes=new ArrayList<>();
    private List<Boolean> isMethod=new ArrayList<>();//true-> method, false-> constructor
    private List<Boolean> hasMoreOptions=new ArrayList<>();
    private List<Integer> paramItemDelays=new ArrayList<>();
    public ParametersGUI(PluginCoder plugin){
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private void createInventory(){
        gui=Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,true);
        ItemStack instructionItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.OAK_SIGN));
        gui.setItem(4,instructionItem);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        gui.setItem(45,PluginCoder.getCoderGUI().getBackItem());
        gui.setItem(53,PluginCoder.getCoderGUI().getNextItem());
    }
    public void updateInventory(String paramContainer,String classType,Inventory previousInv){
        previousInvs.add(previousInv);
        renderedMethods.add(paramContainer);
        classTypes.add(classType);
        paramSelectedIndexes.add(9);
        updateInventory(paramContainer,classType,true);
    }
    public void updateInventoryWithPreviousInstruction(){
        String paramContainer=renderedMethods.get(renderedMethods.size()-1);
        String classType=classTypes.get(classTypes.size()-1);
        updateInventory(paramContainer,classType,false);
    }
    private void updateInventory(String paramContainer,String classType,boolean loadMethods){
        methodsOrConstructors.clear();
        paramTypes.clear();
        for(int i=0;i<7;i++){
            gui.setItem(10+i,null);
            gui.setItem(28+i,null);
            gui.setItem(37+i,null);
        }
        String containerName=paramContainer.replaceAll("^([^(]+)\\s*\\((.+)$","$1");
        List<String> parameters= mainPlugin.getCodeExecuter().getStringParameters(paramContainer);
        if(paramContainer.matches("^if\\s*\\((.+)$")||paramContainer.matches("^else\\s+if\\s*\\((.+)$")
                ||paramContainer.matches("^while\\s*\\((.+)$")){
            paramTypes.add(boolean.class.getTypeName());
            gui.setItem(10,getParamItem(boolean.class.getTypeName(),0,parameters));
            hasMoreOptions.add(false);
        }else if(paramContainer.matches("^delay\\s*\\((.+)$")){
            paramTypes.add(double.class.getTypeName());
            gui.setItem(10,getParamItem(double.class.getTypeName(),0,parameters));
            hasMoreOptions.add(false);
        }else if(paramContainer.matches("^repeat\\s*\\((.+)$")){
            paramTypes.add(double.class.getTypeName());//TODO poner opcion del segundo param del repeat
            gui.setItem(10,getParamItem(double.class.getTypeName(),0,parameters));
            hasMoreOptions.add(true);
        }else if(mainPlugin.getConstructorTranslator().containsKey(containerName)){
            isMethod.add(false);
            if(containerName.equals("Inventory")){
                ContentParams contentParams= new ContentParams(containerName,Arrays.asList(String.class.getTypeName(),int.class.getTypeName()));
                methodsOrConstructors.add(contentParams);
            }else{
                Class constructorClass= mainPlugin.getConstructorTranslator().get(containerName);
                for(Constructor constructor:constructorClass.getConstructors()){
                    List<String> paramTypes= Arrays.stream(constructor.getParameterTypes()).map(p->p.getTypeName()).collect(Collectors.toList());
                    ContentParams contentParams= new ContentParams(containerName,paramTypes);
                    methodsOrConstructors.add(contentParams);
                }
            }
        }else if(mainPlugin.getSelectedPlugin().getObjects().stream().anyMatch(o->o.getName().equals(containerName))){
            isMethod.add(false);
            PluginObject object= mainPlugin.getSelectedPlugin().getObject(containerName);
            for(String constructor:object.getConstructors())checkObjectFunctionParams(object,constructor,containerName);

        }else if(classType.startsWith("PluginObject.")){
            PluginObject object= mainPlugin.getSelectedPlugin().getObject(classType.replace("PluginObject.",""));
            for(String function:object.getFunctions()) checkObjectFunctionParams(object,function,containerName);
        }else{ //execution
            isMethod.add(true);
            if(containerName.equals("teleport")){
                methodsOrConstructors.add(new ContentParams(containerName,Arrays.asList("double","double","double")));
                methodsOrConstructors.add(new ContentParams(containerName,Arrays.asList(World.class.getTypeName(),"double","double","double")));
                methodsOrConstructors.add(new ContentParams(containerName,Arrays.asList(World.class.getTypeName(),"double","double","double","float","float")));
            }
            List<Method> methods=getMethod(containerName,classType);
            for(Method method:methods){
                List<String> paramTypes= Arrays.stream(method.getParameterTypes()).map(p->p.getTypeName()).collect(Collectors.toList());
                ContentParams contentParams= new ContentParams(containerName,paramTypes);
                methodsOrConstructors.add(contentParams);
            }
        }
        hasMoreOptions.add(methodsOrConstructors.size()>1);
        if(methodsOrConstructors.size()>0){
            if(loadMethods)loadBestMethodOrConstructor(parameters);
            else renderContentParams(methodSelectedIndexes.get(methodSelectedIndexes.size()-1),parameters,true);
            if(methodsOrConstructors.size()>1)updateOptions(containerName+"()");
        }
        ItemMeta meta=gui.getItem(4).getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(paramContainer));
        gui.getItem(4).setItemMeta(meta);
        updateInstructionSign();
    }
    private void checkObjectFunctionParams(PluginObject object, String function, String containerName){
        String functionContainer=function.replaceAll("^([^{]+)\\s*\\{(.+)$","$1");
        if(!functionContainer.replaceAll("^([^(]+)\\s*\\((.+)$","$1").equals(containerName))return;
        List<String> functionParams= mainPlugin.getCodeExecuter().getStringParameters(functionContainer);
        List<String> paramTypes= new ArrayList<>();
        Set<String> predictParams=new HashSet<>(object.getProperties());
        predictParams.addAll(functionParams);
        for(String param:functionParams){
            paramTypes.add(PredictType.predictTypeOfVar(mainPlugin.getSelectedPlugin(),param,function,predictParams));
        }
        ContentParams contentParams= new ContentParams(containerName,paramTypes);
        methodsOrConstructors.add(contentParams);
    }
    private void loadBestMethodOrConstructor(List<String> paramNames) {
        boolean methodRendered=false;
        for(int i=0;i<methodsOrConstructors.size();i++){
            if(methodsOrConstructors.get(i).getParamTypes().size()!=paramNames.size())continue;
            methodRendered=isBestMethodOrConstructor(i,paramNames,methodsOrConstructors.get(i).getParamTypes());
            if(!methodRendered)continue;
            methodSelectedIndexes.add(i);
            renderContentParams(i,paramNames,true);break;
        }
        if(!methodRendered){
            methodSelectedIndexes.add(0);
            renderContentParams(0,paramNames,true);
        }
    }
    private boolean isBestMethodOrConstructor(int i,List<String> paramNames,List<String> paramTypes){
        boolean paramsRendered=true;
        for(int j = 0; j<paramTypes.size(); j++){
            String param=paramNames.get(j);
            if(param.equals("null")||param.isEmpty())continue;
            String paramType= PluginCoder.getCoderGUI().getExecutionWriterGUI().getTypeOfExecution(param);
            try {
                if(!paramType.equals(paramTypes.get(j))
                        &&!Class.forName(paramTypes.get(j)).isAssignableFrom(Class.forName(paramType))){
                    paramsRendered=false;break;
                }
            }catch (Exception e){
                paramsRendered=false;break;
            }
        }
        return paramsRendered;
    }
    public void prepareToNextGUI(int paramIndex){
        if(paramIndex>0)paramSelectedIndexes.set(paramSelectedIndexes.size()-1,paramIndex);
        renderedMethods.set(renderedMethods.size()-1,ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim());
    }
    public void renderContentParams(int methodIndex,List<String> parameters,boolean firstRender){
        if(!firstRender&&methodIndex==methodSelectedIndexes.get(methodSelectedIndexes.size()-1))return;
        int index=0;paramTypes.clear();
        methodSelectedIndexes.set(methodSelectedIndexes.size()-1,methodIndex);
        for(int i=0;i<7;i++)gui.setItem(10+i,null);
        ContentParams contentParams=methodsOrConstructors.get(methodIndex);
        for(int i=0;i<7;i++){
            if(contentParams.getParamTypes().size()==i)break;
            String paramType=contentParams.getParamTypes().get(i);
            paramTypes.add(paramType);
            ItemStack paramItem=getParamItem(paramType,i,parameters);
            gui.setItem(10+index,paramItem);
            index++;
        }
    }
    private ItemStack getParamItem(String paramType,int index,List<String> parameters){
        ItemStack paramItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta=paramItem.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(index==0){
            meta.addEnchant(Enchantment.values()[0],1,false);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        boolean nullParam=false;
        if(parameters.size()>index&&!parameters.get(index).trim().isEmpty()){
            meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(parameters.get(index)));
        }else nullParam=true;
        if(nullParam){
            String param=ChatColor.DARK_RED+"null";
            if(PluginCoder.getCoderGUI().getMathGUI().typeIsMath(paramType))param=ChatColor.WHITE+"0";
            else if(paramType.equals(boolean.class.getTypeName()))param=ChatColor.RED+"false";
            meta.setDisplayName(param);
        }
        List<String> lore=new ArrayList<>();
        lore.add(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("dataType")+": "+ChatColor.WHITE+getDisplayType(paramType));
        meta.setLore(lore);
        paramItem.setItemMeta(meta);
        return paramItem;
    }
    public void updateParams(String searchType,int paramIndex){
        int oldParamIndex=paramSelectedIndexes.get(paramSelectedIndexes.size()-1);
        if(oldParamIndex==paramIndex)return;
        paramSelectedIndexes.set(paramSelectedIndexes.size()-1,paramIndex);
        ItemStack oldMethodParamItem= gui.getItem(oldParamIndex);
        if(oldMethodParamItem!=null){
            ItemMeta meta=oldMethodParamItem.getItemMeta();
            meta.removeEnchant(Enchantment.values()[0]);
            oldMethodParamItem.setItemMeta(meta);

        }
        ItemStack methodParamItem= gui.getItem(paramIndex);
        ItemMeta meta=methodParamItem.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addEnchant(Enchantment.values()[0],1,false);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        methodParamItem.setItemMeta(meta);
        Set<String> possibleParams=new HashSet<>();
        renderElements.clear();
        elementPage=0;
        Map<String,List<String>> reverseTranslation= mainPlugin.getCodeUtils().getReverseTranslation();
        boolean isEnum=false;
        PluginCoder.getCoderGUI().getExecutionWriterGUI().updateVariables();
        for(String variable: PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariables()){
            String varType= PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariableTypes().get(variable);
            try{
                if(varType.equals(searchType)||Class.forName(searchType).isAssignableFrom(Class.forName(varType)))possibleParams.add(variable);
                searchPossibleParams(variable,varType,searchType,0,possibleParams,reverseTranslation);
            }catch (Exception e){}
        }
        try{
            Class paramClass=Class.forName(searchType);
            if(paramClass.isEnum()){
                isEnum=true;
                for(Object enumValue:(Object[])paramClass.getMethod("values").invoke(null)){
                    possibleParams.add(enumValue.toString());
                }
                for(String var: PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariables()){
                    if(!var.getClass().equals(String.class))continue;
                    try {
                        paramClass.getMethod("valueOf").invoke(var);
                        possibleParams.add(var);
                    }catch (Exception e){}
                }
            }
        }catch (Exception e){}
        List<String> paramsSorted;
        if(isEnum){
            List<String> enumParamsSorted=possibleParams.stream().sorted().collect(Collectors.toList());
            paramsSorted= PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariables().stream().filter(var->
                    PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariableTypes().get(var).equals(String.class.getTypeName())).collect(Collectors.toList());
            paramsSorted.addAll(enumParamsSorted);
        }
        else paramsSorted=possibleParams.stream().sorted(Comparator.comparing(par->par.length())).collect(Collectors.toList());
        addRenderParamItems(paramsSorted,0);
        for(int pages=1;pages<(paramsSorted.size()/14);pages++){
            final int pageCount=pages*14;
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin,() -> {
                addRenderParamItems(paramsSorted,pageCount);
            },pages);
        }
        ItemStack vars=new ItemStack(Material.CHEST);
        meta=vars.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("variablesTitle"));
        vars.setItemMeta(meta);
        gui.setItem(48,vars);
        ItemStack newObject=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.CRAFTING_TABLE));
        meta=newObject.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("newObject"));
        newObject.setItemMeta(meta);
        gui.setItem(50,newObject);
        renderElements();
    }
    private void addRenderParamItems(List<String> params,int pageCount){
        for(int index=0;index<14;index++){
            if(index+pageCount>=params.size())break;
            String param=params.get(index+pageCount);
            ItemStack paramItem;
            Material itemMaterial=null;
            try {itemMaterial=Material.valueOf(param);}
            catch (Exception e){}
            if(itemMaterial!=null)  {
                paramItem=new ItemStack(itemMaterial);
                if(!paramItem.getType().isItem()||param.startsWith("LEGACY_"))continue;
            }
            else  paramItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
            ItemMeta itemMeta=paramItem.getItemMeta();
            if(itemMeta==null)continue;
            itemMeta.setDisplayName(ChatColor.WHITE+param);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            paramItem.setItemMeta(itemMeta);
            renderElements.add(paramItem);
        }
    }
    private void searchPossibleParams(String variable,String varType,String searchType,int iterations,Set<String> possibleParams,Map<String,List<String>> reverseTranslation) throws ClassNotFoundException {
       if(iterations==2)return;
       if(mainPlugin.getCodeUtils().isFinishMethod(varType))return;
        if(!varType.startsWith("PluginObject.")) {
            Arrays.stream(Class.forName(varType).getMethods()).forEach(method -> {
                String methodName= mainPlugin.getCodeUtils().getMethodName(method,reverseTranslation.get(method.getName()));
                if(methodName==null)return;
                if(varType.equals(method.getReturnType().getTypeName()))return;
                //TODO cambiar y poner el tipo del parámetro
                String newVar=variable+"."+methodName+(method.getParameters().length>0&&!methodName.endsWith("()")?"()":"");
                String params="";
                for(Class param:method.getParameterTypes()) params+=getDisplayType(param.getTypeName())+",";
                params=params.isEmpty()?"":params.substring(0,params.length()-1);
                newVar=newVar.replace("()","("+params+")");
                String newVarType=method.getReturnType().getTypeName();
                try {
                    if(!newVarType.equals(searchType)&&!Class.forName(searchType).isAssignableFrom(method.getReturnType())){
                        searchPossibleParams(newVar,newVarType,searchType,iterations+1,possibleParams,reverseTranslation);
                    }else possibleParams.add(newVar);
                }catch (Exception e){}
            });
        }else{
            PluginObject object= mainPlugin.getSelectedPlugin().getObject(varType.replace("PluginObject.",""));
            Map<String,String> inicVarTypes= InicVars.getInicVarTypes(object.getPlugin(),"",varType);
            for(String property:object.getProperties()){
                searchPossibleParams(variable+"."+property,inicVarTypes.get(property),searchType,0,possibleParams,reverseTranslation);
            }
            for(String function:object.getFunctions()){
                String functionName=function.replaceAll("^([^{]+)\\{(.*)$","$1");
                searchPossibleParams(variable+"."+functionName,PredictType.predictReturnType(object,function),searchType,0,possibleParams,reverseTranslation);
            }
        }
   }
    public boolean updateOptions(String methodOrConstructorName){
        elementPage=0;
        renderElements.clear();
        paramSelectedIndexes.set(paramSelectedIndexes.size()-1,9);
        int methodSelectedIndex=methodSelectedIndexes.get(methodSelectedIndexes.size()-1);
        for(int i=0;i<methodsOrConstructors.size();i++){
            ContentParams contentParams=methodsOrConstructors.get(i);
            createItemsToRender(i,methodOrConstructorName,methodSelectedIndex,contentParams.getParamTypes());
        }
        gui.setItem(48,gui.getItem(49).clone());
        gui.setItem(50,gui.getItem(49).clone());
        renderElements();
        return true;
    }
    private void createItemsToRender(int i,String methodName,int methodSelectedIndex,List<String> paramTypes){
        String paramsString=methodName.substring(0,methodName.length()-1);
        for(String param:paramTypes){
            paramsString+=getDisplayType(param)+",";
        }
        paramsString=paramsString.endsWith(",")?paramsString.substring(0,paramsString.length()-1)+")":methodName;
        ItemStack methodItem=new ItemStack(Material.BOOK);
        ItemMeta meta=methodItem.getItemMeta();
        if(methodSelectedIndex==i){
            meta.addEnchant(Enchantment.values()[0],1,false);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setDisplayName(ChatColor.GOLD+paramsString);
        methodItem.setItemMeta(meta);
        renderElements.add(methodItem);
    }
    public String getDisplayType(String type){
        boolean array=false;
        String paramName=type.replaceAll("^(.+)\\.([^.]+)$","$2");
        if(paramName.endsWith("[]")){
            array=true;
            paramName=paramName.substring(0,paramName.length()-2);
        }
        if(paramName.equals("String"))paramName=PluginCoder.getCoderGUI().getGuiText("text");
        else if(PluginCoder.getCoderGUI().getMathGUI().typeIsMath(type))paramName=PluginCoder.getCoderGUI().getGuiText("number");
        if(array)paramName="["+paramName+"]";

        return paramName;
    }
    public void renderElements(){
        int index=0;
        for(int i=0;i<7;i++){
            gui.setItem(28+i,null);
            gui.setItem(37+i,null);
        }
        List<ItemStack> pageElements;
        if(renderElements.size()>14*(elementPage+1))pageElements=renderElements.subList(14*elementPage,14*(elementPage+1));
        else pageElements=renderElements.subList(14*elementPage,renderElements.size());
        for(ItemStack element:pageElements){
            gui.setItem(28+index,element);
            if(index==6)index+=3;
            else index++;
        }
    }
    public void saveToParametersGUI(String param,boolean closingInventoryWithoutReturning){
        int selectedIndex= paramSelectedIndexes.get(paramSelectedIndexes.size()-1);
        updateInventoryWithPreviousInstruction();
        paramSelectedIndexes.set(paramSelectedIndexes.size()-1,selectedIndex);
        updateMethodParam(param);
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
    public void updateMethodParam(String param){
        int paramSelectedIndex=paramSelectedIndexes.get(paramSelectedIndexes.size()-1);
        ItemStack paramItem=gui.getItem(paramSelectedIndex);
        if(param.isEmpty())param="null";
        ItemMeta meta=paramItem.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(param));
        paramItem.setItemMeta(meta);
        updateInstructionSign();
    }
    public void saveChanges(boolean closingInventoryWithoutReturning) {
        Inventory previousInv=previousInvs.remove(previousInvs.size()-1);
        String instruction=renderedMethods.remove(renderedMethods.size()-1);
        classTypes.remove(classTypes.size()-1);
        if(!methodSelectedIndexes.isEmpty())methodSelectedIndexes.remove(methodSelectedIndexes.size()-1);
        paramSelectedIndexes.remove(paramSelectedIndexes.size()-1);
        if(previousInv.equals(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui())){
            PluginCoder.getCoderGUI().getExecutionWriterGUI().saveToExecutiongWritterGUI(instruction,closingInventoryWithoutReturning);
        }else if(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(previousInv))){
            PluginCoder.getCoderGUI().getFunctionGUI().saveToFunctionGUI(instruction,closingInventoryWithoutReturning);
        }else if(previousInv.equals(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI())){
            PluginCoder.getCoderGUI().getConstructorsGUI().saveToConstructorsGUI(instruction,closingInventoryWithoutReturning);
        }
    }
    public void updateInstructionSign(){
        ItemStack instructionSign=gui.getItem(4);
        ItemMeta meta=instructionSign.getItemMeta();
        String method=ChatColor.stripColor(meta.getDisplayName()).trim();
        method=method.endsWith(")")?method.replaceAll("^([^(]+)\\((.+)$","$1("):method+"(";
        for(int i=0;i<7;i++){
            ItemStack paramItem=gui.getItem(10+i);
            if(paramItem==null||paramItem.getType()==Material.AIR)break;
            method+=ChatColor.stripColor(paramItem.getItemMeta().getDisplayName()).trim()+",";
        }
        method=(method.endsWith(",")?method.substring(0,method.length()-1):method)+")";
        renderedMethods.set(renderedMethods.size()-1,method);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                "&f"+PluginCoder.getCoderGUI().putTextColor(method)));
        instructionSign.setItemMeta(meta);
    }
    public void selectMethod(ItemStack newMethodItem, int methodIndex) {
        int oldMethodIndex=methodSelectedIndexes.get(methodSelectedIndexes.size()-1);
        if(oldMethodIndex==methodIndex)return;
        ItemStack oldMethodItem=renderElements.get(oldMethodIndex);
        ItemMeta meta=oldMethodItem.getItemMeta();
        meta.removeEnchant(Enchantment.values()[0]);
        oldMethodItem.setItemMeta(meta);
        if(methodIndex/14==oldMethodIndex/14){
            gui.setItem(oldMethodIndex%7+(oldMethodIndex%14<7?28:37),oldMethodItem);
        }
        meta=newMethodItem.getItemMeta();
        meta.addEnchant(Enchantment.values()[0],1,false);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        newMethodItem.setItemMeta(meta);
        renderElements.set(methodIndex,newMethodItem);
    }
    public void returnPage(Player p) {
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        saveChanges(false);
        p.openInventory(previousInv);
    }

    public void returnHome(Player p) {
        saveChanges(true);
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public boolean previousPage(){
        if(elementPage>0)elementPage--;
        else return false;
        renderElements();
        return true;
    }
    public boolean nextPage(){
        if(renderElements.size()>(elementPage+1)*14)elementPage++;
        else return false;
        renderElements();
        return true;
    }
    private List<Method> getMethod(String method,String type){
        List<Method> methods=new ArrayList<>();
        List<String> translatedMethods= mainPlugin.getMethodTranslator().get(method+"()");
        try{
            Arrays.stream(Class.forName(type).getMethods()).filter(m->translatedMethods.contains(m.getName())
            &&m.getParameters().length>0).forEach(m->methods.add(m));
            methods.sort(Comparator.comparing((Method m)->m.getParameters().length).thenComparing((Method m)->
                    m.getReturnType().getTypeName()));
        }catch (Exception e){}
        return methods;
    }
    public Inventory getGUI() {
        return gui;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public int getElementPage() {
        return elementPage;
    }

    public boolean hasMoreOptions() {
        return hasMoreOptions.get(hasMoreOptions.size()-1);
    }
}
