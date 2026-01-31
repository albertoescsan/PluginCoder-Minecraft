package berty.plugincoder.utils;

import berty.plugincoder.interpreter.classes.scoreboard.Scoreboard;
import berty.plugincoder.interpreter.objects.PluginMethod;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.predictor.PredictType;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CodeUtils {

    private PluginCoder pluginCoder;
    public CodeUtils(PluginCoder pluginCoder){
        this.pluginCoder=pluginCoder;
    }
    public Map<String,List<String>> getMethodsOfType(String type){
        if(type==null)return new HashMap<>();
        type= clearContainerType(type);
        Map<String,List<String>> methods=new HashMap<>();
        if(isFinishMethod(type))return methods;
        //es un objeto del plugin
        if(type.startsWith("PluginObject.")){
            PluginObject object=pluginCoder.getSelectedPlugin().getObject(type.replace("PluginObject.",""));
            methods.put("text", Arrays.asList(String.class.getTypeName()));
            for(String property:object.getProperties()){
                methods.put(property,Arrays.asList(PredictType.predictTypeOfObjectProperty(object,property)));
                if(!(type.equals("PluginObject.Plugin")&&property.equals("name")))methods.put(property+"()",Arrays.asList(void.class.getTypeName()));
            }for(String function:object.getFunctions()){
                String functionName=function.replaceAll("^([^{]+)\\{(.+)}$","$1");
                if(functionName.endsWith(")"))functionName=functionName.replaceAll("^([^(]+)\\((.+)$","$1")+"()";
                methods.put(functionName,Arrays.asList(PredictType.predictReturnType(object,function)));
            }
        }else{
            Map<String,List<String>> reverseTranslation=getReverseTranslation();
            try {
                Class<?> typeClass = Class.forName(type);
                List<PluginMethod> typeClassMethods=pluginCoder.getMethods().stream().filter(method->method.getTranslatedMethodClasses()
                .values().stream().anyMatch(classTypes->classTypes.stream().anyMatch(classType-> {
                 try {return Class.forName(classType).isAssignableFrom(typeClass);} catch (Exception e) {return false;}})))
                .collect(Collectors.toList());
                Arrays.stream(typeClass.getMethods()).filter(method->typeClassMethods.stream().anyMatch(
                pluginMethod -> pluginMethod.getTranslatedMethodClasses().containsKey(method.getName())
                )).forEach(method->{
                  String pluginMethodName=getMethodName(method,reverseTranslation.get(method.getName()));
                  if(methods.get(pluginMethodName)==null)methods.put(pluginMethodName,new ArrayList<>());
                  String returnClass=method.getGenericReturnType().getTypeName();
                  returnClass= pluginCoder.getCodeUtils().checkCustomType(returnClass);
                  methods.get(pluginMethodName).add(returnClass);
                });
            } catch (ClassNotFoundException e) {}
        }
        for(String method:methods.keySet()){
            methods.put(method,new ArrayList<>(new HashSet<>(methods.get(method))));
        }
        return methods;
    }
    public String clearContainerType(String type) {
        if(type==null)return null;
        type=type.replaceAll("^([^<]+)<(.*)>$","$1");
        if(type.startsWith("List."))type=List.class.getTypeName();
        else if(type.startsWith("Set."))type=Set.class.getTypeName();
        else if(type.startsWith("Map."))type=Map.class.getTypeName();
        else if(type.startsWith("Collection."))type=Collection.class.getTypeName();
        return type;
    }

    public Map<String,List<String>> getReverseTranslation(){
        Map<String,List<String>> reverseTranslation=new HashMap<>();
        for(PluginMethod method:pluginCoder.getMethods()){
            for(String translation:method.getTranslatedMethodClasses().keySet()){
                if(reverseTranslation.get(translation)==null)reverseTranslation.put(translation,new ArrayList<>());
                reverseTranslation.get(translation).add(method.getName());
            }
        }
        return reverseTranslation;
    }
    public boolean isFinishMethod(String returnType) {
        return returnType==null?true:returnType.equals(void.class.getTypeName())||
                returnType.equals(boolean.class.getTypeName())||returnType.equals(double.class.getTypeName())||
                returnType.equals(int.class.getTypeName())||returnType.equals(float.class.getTypeName())||returnType.equals(long.class.getTypeName());
    }
    public String getTypeOfExecution(Plugin plugin, String execution, Map<String,String> variableTypes){
        if(execution.trim().isEmpty())return Void.class.getTypeName();
        //execution is list
        if(execution.startsWith("[")&&execution.endsWith("]")){
            if(execution.matches("^\\[\\s*\\]$"))return "List.";
            String listType="";
            for(String listElement:pluginCoder.getCodeExecuter().getStringParameters("("+execution.substring(1,execution.length()-1)+")")){
                String elementType=getTypeOfExecution(plugin,listElement,variableTypes);
                if(listType.isEmpty())listType=elementType;
                else if(!listType.equals(elementType)){
                    if(listType.equals("int")&&elementType.equals("double"))listType="double";
                    else if(!(listType.equals("double")&&elementType.equals("int")))listType=Object.class.getTypeName();
                }
            }
            if(listType.equals("int"))return "List."+Integer.class.getTypeName();
            if(listType.equals("double"))return "List."+Double.class.getTypeName();
            else return "List."+listType;
        }//execution is map
        else if(execution.startsWith("{")&&execution.endsWith("}")){
            if(execution.matches("^\\{\\s*}$"))return "Map.";
            String keyElements="";
            String valueElements="";
            for(String keyValue:pluginCoder.getCodeExecuter().getStringParameters("("+execution.substring(1,execution.length()-1)+")")){
                String[] keyValueContainer=getElementsBySeparator(keyValue,':');
                keyElements+=keyValueContainer[0]+",";valueElements+=keyValueContainer[1]+",";
            }
            keyElements="["+keyElements.substring(0,keyElements.length()-1)+"]";
            valueElements="["+valueElements.substring(0,valueElements.length()-1)+"]";
            String keysType=getTypeOfExecution(plugin,keyElements,variableTypes).replace("List.","");
            String valuesType=getTypeOfExecution(plugin,valueElements,variableTypes).replace("List.","");
            //formato Map.Type1-Type2
            return "Map."+keysType+"-"+valuesType;
        }
        //execution is constructor
        String constructorName=execution.replaceAll("^([^(]+)\\((.+)$","$1");
        Class constructorClass=pluginCoder.getConstructorTranslator().get(constructorName);
        if(constructorClass!=null)return constructorClass.getTypeName();
        PluginObject object=plugin.getObject(constructorName);
        if(object!=null)return "PluginObject."+object.getName();
        //execution is number
        try{
            double num=Double.parseDouble(execution);
            if((num+"").endsWith(".0"))return "int";
            else return "double";
        }catch (Exception e){}
        Map<String,Object> vars=new HashMap<>(variableTypes);
        vars.put("plugin",plugin.getMainObjectInstance());
        String varInstructionType= pluginCoder.getCodeExecuter().getVarInstructionType(execution,vars);
        if(varInstructionType.equals("Math")){
            for(String mathElement:pluginCoder.getMath().getNumbers(execution)){
                if(!getTypeOfExecution(plugin,mathElement,variableTypes).equals("int"))return "double";
            }
            return "int";
        }else if(varInstructionType.equals("Boolean"))return boolean.class.getTypeName();
        else if(varInstructionType.equals("String"))return String.class.getTypeName();
        String[] methods=execution.trim().isEmpty()?new String[]{}:pluginCoder.getCodeExecuter().getMethodsOfInstruction(execution);
        String variableType=variableTypes.get(methods[0]);
        if(variableType==null){
            if(execution.equalsIgnoreCase("null"))return null;
            else if(execution.equalsIgnoreCase("true")||execution.equalsIgnoreCase("false"))return boolean.class.getTypeName();
            else return String.class.getTypeName();
        }
        if(variableType.startsWith("List.")){
            if(methods.length==1)return variableType;
            else variableType=List.class.getTypeName();
        }
        else if(variableType.startsWith("Map.")){
            if(methods.length==1)return variableType;
            else variableType=Map.class.getTypeName();
        }
        else if(variableType.startsWith("Set.")){
            if(methods.length==1)return variableType;
            else variableType=Set.class.getTypeName();
        }
        List<String> executedTypes=new ArrayList<>();
        executedTypes.add(variableType);
        List<String> executionTypes=Arrays.asList(variableType);
        for(int i=1;i<methods.length;i++) {
            Map<String,List<String>> methodTypes=getMethodsOfType(executedTypes.get(executedTypes.size()-1));
            String method = methods[i].replaceAll("^([^(]+)\\((.*)\\)$", "$1");//nombre del metodo
            if (method.length() != methods[i].length()) method += "()";
            List<Object> paramTypes=new ArrayList<>();
            for(String param:pluginCoder.getCodeExecuter().getStringParameters(methods[i])){
                paramTypes.add(clearContainerType(getTypeOfExecution(plugin,param,variableTypes)));
            }
            Method javaMethod=getJavaMethod(executionTypes,method,paramTypes);
            String returnType;
            if(javaMethod!=null) returnType=javaMethod.getGenericReturnType().getTypeName();
            else returnType=methodTypes.get(method).get(0);//TODO ver la mejor opcion de returnType para funciones de PluginObject
            try{
                if(returnType==null)returnType=methodTypes.get(method).get(0);
            }catch (Exception e){return null;}
            if(returnType.startsWith(List.class.getTypeName())){
                returnType=returnType.replaceAll(List.class.getTypeName()+"<(.+)>","List.$1");
            }else if(returnType.startsWith(Set.class.getTypeName())){
                returnType=returnType.replaceAll(Set.class.getTypeName()+"<(.+)>","Set.$1");
            }else if(returnType.startsWith(Map.class.getTypeName())){
                returnType=returnType.replaceAll(Map.class.getTypeName()+"<(.+),\\s*(.+)>","Map.$1-$2");
            }
            executedTypes.add(returnType);
            executionTypes=methodTypes.get(method);
        }
        executedTypes.remove(0);
        String varType=variableTypes.get(methods[0]);
        for(int i=0;i<executedTypes.size();i++){
            String executedType=executedTypes.get(i);
            if(methods[i+1].equalsIgnoreCase("keys")){
                String setType=varType.replaceAll("Map\\.(.+)-(.+)","$1" );
                varType="Set."+setType;
            }else if(methods[i+1].equalsIgnoreCase("values")){
                String colType=varType.replaceAll("Map\\.(.+)-(.+)","$2");
                varType="Collection."+colType;
            }else if(methods[i+1].equalsIgnoreCase("get")){
                if(executedType.startsWith("List.")){
                    varType=varType.replaceAll("List\\.(.+)","$1" );
                }else if(executedType.startsWith("Map.")){
                    varType=varType.replaceAll("Map\\.(.+)-(.+)","$2" );
                }
            }else varType=executedType;
        }
        return varType;
    }
    public Method getJavaMethod(List<String>executionTypes,String method,List<Object> paramTypes){
        try{
            for(String type:executionTypes){
                Class executionClass=Class.forName(type);
                Method javaMethod=pluginCoder.getCodeExecuter().getMethod(executionClass,method,paramTypes,false);
                if(javaMethod!=null)return javaMethod;
            }
        }catch (Exception e){}
        return null;
    }
    public String[] getElementsBySeparator(String instruction,char separator){
        String newInstruction="";
        int parentesisCount=0;
        for(Character c:instruction.toCharArray()) {
            if(c.equals('('))parentesisCount++;
            else if(c.equals(')'))parentesisCount--;
            if(parentesisCount==0)newInstruction+=c;
            else {
                if(!c.equals(separator))newInstruction+=c;
                else newInstruction+='|';
            }
        }
        String regrex=separator=='.'?"\\.": String.valueOf(separator);
        String[] elements=newInstruction.split(regrex);
        for(int i=0;i<elements.length;i++)elements[i]=elements[i].replace("|", String.valueOf(separator));
        return elements;
    }

    public String getMethodName(Method method,List<String> methods) {
        if(methods.size()>1){
            if(method.getParameterCount()==0)return methods.stream().filter(me->!me.endsWith("()")).findFirst().get();
            else return methods.stream().filter(me->me.endsWith("()")).findFirst().get();
        }else return methods.get(0);
    }
    public Material getVersionedMaterial(String material) {
        if("MAP".equals(material)) {
            if(pluginCoder.getVersionNumber() < 13)return Material.getMaterial("EMPTY_MAP");
            return Material.MAP;
        }else if("FILLED_MAP".equals(material)) {
            if (pluginCoder.getVersionNumber() < 13)return Material.getMaterial("MAP");
            return Material.FILLED_MAP;
        }else if("OAK_SIGN".equals(material)) {
            if (pluginCoder.getVersionNumber() < 14)return Material.getMaterial("SIGN");
            return Material.OAK_SIGN;
        }else if ("WRITABLE_BOOK".equals(material)) {
            if(pluginCoder.getVersionNumber() < 14)return Material.getMaterial("BOOK_AND_QUILL");
            return Material.WRITABLE_BOOK;
        }else if("CRAFTING_TABLE".equals(material)) {
            if (pluginCoder.getVersionNumber() < 13)return Material.getMaterial("WORKBENCH");
            return Material.CRAFTING_TABLE;
        }else if ("WHITE_STAINED_GLASS_PANE".equals(material)) {
            if(pluginCoder.getVersionNumber() < 13)return Material.getMaterial("STAINED_GLASS_PANE");
            return Material.WHITE_STAINED_GLASS_PANE;
        }else if ("GRASS_BLOCK".equals(material)) {
            if(pluginCoder.getVersionNumber() < 13)return Material.getMaterial("GRASS");
            return Material.GRASS_BLOCK;
        }else if("SKELETON_SKULL".equals(material)) {
            if(pluginCoder.getVersionNumber() < 13)return Material.getMaterial("SKULL_ITEM");
            return Material.SKELETON_SKULL;
        }else if("ENDER_EYE".equals(material)) {
            if(pluginCoder.getVersionNumber() < 13)return Material.getMaterial("EYE_OF_ENDER");
            return Material.ENDER_EYE;
        }else if("COMMAND_BLOCK".equals(material)) {
            if(pluginCoder.getVersionNumber() < 13)return Material.getMaterial("COMMAND");
            return Material.COMMAND_BLOCK;
        }
        return Material.getMaterial(material);
    }

    public String checkCustomType(String type){
        Class classType;
        try{
            classType=Class.forName(type);
        }catch (Exception e){return type;}
        if(org.bukkit.scoreboard.Scoreboard.class.isAssignableFrom(classType))type=Scoreboard.class.getTypeName();
        return type;
    }
    public static Class<?> primitiveToWrapper(Class<?> clazz) {
        if (clazz == boolean.class) return Boolean.class;
        if (clazz == byte.class)    return Byte.class;
        if (clazz == char.class)    return Character.class;
        if (clazz == short.class)   return Short.class;
        if (clazz == int.class)     return Integer.class;
        if (clazz == long.class)    return Long.class;
        if (clazz == float.class)   return Float.class;
        if (clazz == double.class)  return Double.class;
        if (clazz == void.class)    return Void.class;
        return clazz;
    }
    public static Class<?> getPrimitiveType(String typeName) {
        if (typeName == null) return null;
        if (typeName.equals("boolean")) return boolean.class;
        if (typeName.equals("byte"))    return byte.class;
        if (typeName.equals("short"))   return short.class;
        if (typeName.equals("int"))     return int.class;
        if (typeName.equals("long"))    return long.class;
        if (typeName.equals("float"))   return float.class;
        if (typeName.equals("double"))  return double.class;
        if (typeName.equals("char"))    return char.class;
        if (typeName.equals("void"))    return void.class;
        return null;
    }
}
