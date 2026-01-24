package berty.plugincoder.predictor;

import berty.plugincoder.interpreter.objects.PluginMethod;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PredictType {

    public static PluginCoder mainPlugin;
    public static Set<String> predictTypeOfVar(Plugin plugin,String var, List<String> functions,Set<String> vars){
        Set<String> types=new HashSet<>();
        for(String function:functions){
            predictTypeOfVar(plugin,var,function,types,new HashSet<>(vars));
            if(types.size()==1)break;
        }
        return types;
    }
    public static String predictTypeOfVar(Plugin plugin,String var, String function,Set<String> vars){
        return predictTypeOfVar(plugin,var,function,new HashSet<>(),new HashSet<>(vars));
    }
    public static String predictTypeOfVar(Plugin plugin,String var, String function,Set<String> types,Set<String> vars){
        List<String> parameters=mainPlugin.getCodeExecuter().getStringParameters(function.replaceAll("^([^{]+)\\{(.+)$","$1"));
        vars.addAll(parameters);
        predictTypesInFunction(plugin,function,var,types,new HashSet<>(vars));
        if(types.size()==1)return types.stream().findFirst().get();
        if(parameters.contains(var)){
            String functionName=function.replaceAll("^([^{]+)\\{(.+)$","$1").replaceAll("^([^(]+)\\((.+)$","$1");
            int paramIndex=parameters.indexOf(var);
            Set<String> commonVars=new HashSet<>(mainPlugin.getPluginVars(plugin).keySet());
            BiFunction<String,List<String>,Void> checkFunctionUsage=(func,initialVars)->{
                if(func.equals(function))return null;
                Set<String> functionVars=new HashSet<>(commonVars);
                functionVars.addAll(initialVars);
                checkFunctionUsageInFunction(plugin,functionName,func,paramIndex,parameters.size(),types,functionVars);
                return null;
            };
            plugin.getAllObjects().stream().forEach(obj->{
                Function<String,Void> checkFunctionUsageInObject=(func)->{
                    List<String> functionVars=new ArrayList<>(obj.getProperties());
                    functionVars.addAll(mainPlugin.getCodeExecuter().getStringParameters(func.replaceAll("^([^{]+)\\{(.+)$","$1")));
                    checkFunctionUsage.apply(func,functionVars);
                    return null;
                };
                obj.getFunctions().stream().forEach(func->checkFunctionUsageInObject.apply(func));
                obj.getConstructors().stream().forEach(func->checkFunctionUsageInObject.apply(func));
            });
            //comandos
            plugin.getCommands().stream().forEach(command ->checkFunctionUsage.apply(command.getFunction(),new ArrayList<>(command.getCommandVars().keySet())));
            //eventos
            plugin.getListener().stream().forEach(event ->checkFunctionUsage.apply(event,Arrays.asList("event")));
        }
        if(types.size()==1)return types.stream().findFirst().get();
        return Object.class.getTypeName();
    }
    private static String checkContainerPrediction(String type,Plugin plugin,List<String> functions,String var,Set<String> vars){
        if(!(type.equals(List.class.getTypeName())||type.equals(Set.class.getTypeName())||
                type.equals(Map.class.getTypeName())||type.equals(Collection.class.getTypeName())))return type;
        String container=type.replaceAll("^(.+)\\.([^.]+)$","$2");
        if(!container.equals("Map"))return container+"."+predictTypeOfContainer(plugin,functions,var,container,false,vars);
        String keyType=predictTypeOfContainer(plugin,functions,var,container,true,vars);
        String valueType=predictTypeOfContainer(plugin,functions,var,container,false,vars);
        return "Map."+keyType+"-"+valueType;
    }
    private static void checkFunctionUsageInFunction(Plugin plugin,String functionName,String function,int paramIndex,int paramNum,Set<String> types,Set<String> vars){
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            if(types.size()==1)break;
            if(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)){
                checkFunctionUsageInFunction(plugin,functionName,instruction,paramIndex,paramNum,types,vars);
                continue;
            }
            if(instruction.matches("^\\s*([a-zA-Z0-9_]+)\\s*=(.+)$")){
                String varValue=instruction.replaceAll("^\\s*([a-zA-Z0-9_]+)\\s*=(.+)$","$2");
                checkFunctionUsageInInstruction(plugin,instruction,varValue,functionName,function,paramIndex,paramNum,types,vars);
                continue;
            }
            for(String method:mainPlugin.getCodeExecuter().getMethodsOfInstruction(instruction)){
                checkFunctionUsageInInstruction(plugin,instruction,method,functionName,function,paramIndex,paramNum,types,vars);
            }
        }
    }
    private static void checkFunctionUsageInInstruction(Plugin plugin,String instruction,String method,String functionName,String function,int paramIndex,int paramNum,Set<String> types,Set<String> vars){
        String methodName=method.replaceAll("^([^(]+)\\((.+)$","$1");
        if(!methodName.equals(functionName))return;
        List<String> methodParams=mainPlugin.getCodeExecuter().getStringParameters(method);
        if(methodParams.size()!=paramNum)return;
        String param=methodParams.get(paramIndex);
        String executionVar=mainPlugin.getCodeExecuter().getMethodsOfInstruction(instruction)[0];
        if(!param.matches("^([0-9]+)$")&&param.matches("^([a-zA-Z0-9_]+)$"))predictTypeOfVar(plugin,param,function,types,vars);
        else predictTypesOfVarEquality(plugin,param,"",types,new HashSet<>(Arrays.asList(executionVar)));
    }
    private static void predictTypesInFunction(Plugin plugin,String function,String var,Set<String> types,Set<String> vars){
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            if (!mainPlugin.getCodeExecuter().instructionIsFunction(instruction)) {
                predictTypeInParams(plugin,instruction,var,types);
                if(instruction.matches("^\\s*([a-zA-Z0-9_]+)\\s*=\\s*(.+)$")){
                    String newVar=instruction.replaceAll("^\\s*([a-zA-Z0-9_]+)\\s*=\\s*(.+)$","$1");
                    vars.add(newVar);
                }
                //var1 = var2.method
                if(instruction.matches("^\\s*"+var+"\\s*=\\s*(.+)$"))predictTypesOfVarEquality(plugin,instruction,var,types,vars);
                //var1.method
                else if(instruction.matches("^([^.(]*)"+var+"\\.(.+)$"))predictTypeOfExecutableVar(plugin,instruction,var,types);

            }else if(instruction.matches("^for\\s*\\((.+)\\)\\s*\\{(.+)$")){
                String[] forContent=mainPlugin.getCodeExecuter().getStringParameters(instruction.replaceAll("^([^{]+)\\s*\\{(.+)$","$1")).get(0).split(":");
                vars.add(forContent[0]);
                if(forContent[0].equals(var)&&(forContent[1].contains("->") ||forContent[1].contains("<-"))){types.add("double");break;}
                else if(forContent[1].equals(var)||forContent[1].matches("^"+var+"\\.(.+)$")){
                    predictTypeOfExecutableVar(plugin,forContent[1],var,types);
                    predictTypesInFunction(plugin,instruction,var,types,new HashSet<>(vars));
                    if(types.isEmpty())types.add("List."+predictTypeOfVar(plugin,forContent[0],instruction,vars));
                }else if(forContent.length==3&&forContent[3].equals(var)){types.add("double");break;}//incrementador*/
                else predictTypesInFunction(plugin,instruction,var,types,new HashSet<>(vars));

            }else if(instruction.matches("^if\\s*\\((.+)\\)\\s*\\{(.+)$")||
                    instruction.matches("^else\\s*if\\s*\\((.+)\\)\\s*\\{(.+)$")||
                    instruction.matches("^while\\s*\\((.+)\\)\\s*\\{(.+)$")){
                predictTypesInBooleanExpression(plugin,instruction,var,types,vars);
                predictTypesInFunction(plugin,instruction,var,types,new HashSet<>(vars));
            }else if(instruction.matches("^else\\s*\\{(.+)$"))predictTypesInFunction(plugin,instruction,var,types,new HashSet<>(vars));
            else if(instruction.matches("^delay\\s*\\((.+)\\)\\s*\\{(.+)$")
            ||instruction.matches("^repeat\\s*\\((.+)\\)\\s*\\{(.+)$")){
                String content=instruction.replaceAll("^([^(]+)\\s*\\((.+)\\)\\s*\\{(.+)$","$2");
                if(content.matches("^(.*)([+\\-/%(]*)"+var+"([+\\-/%)]*)(.*)$")){types.add(double.class.getTypeName());break;}
                String varExecution=content.replaceAll("^(.*)([+\\-/%(]*)"+var+"\\.(.+)([+\\-/%)]*)(.*)$",var+".$3");
                if(varExecution.matches("^"+var+"\\.(.*)$"))predictTypeOfExecutableVar(plugin,varExecution,var,types);
                predictTypesInFunction(plugin,instruction,var,types,vars);
            }else predictTypeInParams(plugin,instruction,var,types);
            if(types.size()==1)break;
        }
    }
    private static void predictTypeInParams(Plugin plugin,String instruction,String var,Set<String> types){
        if(!instruction.contains(var))return;
        Set<String> predictedTypes=new HashSet<>();
        String[] methods=mainPlugin.getCodeExecuter().getMethodsOfInstruction(instruction);
        for(int i=1;i<methods.length;i++){
            List<String> params=mainPlugin.getCodeExecuter().getStringParameters(methods[i]);
            if(params.isEmpty())continue;
            String methodName=methods[i].replaceAll("^\\s*([a-zA-Z0-9_]+)\\s*\\((.+)\\)$","$1()");
            PluginMethod pluginMethod=mainPlugin.getMethod(methodName);
            List<Method> javaMethods=new ArrayList<>();
            if(pluginMethod!=null){
                Set<String> methodClassTypes=new HashSet<>();
                pluginMethod.getTranslatedMethodClasses().values().stream().forEach(classType->methodClassTypes.addAll(classType));
                methodClassTypes.stream().forEach(classType->{
                    try {
                        Arrays.stream(Class.forName(classType).getMethods()).filter(method -> pluginMethod.getTranslatedMethodClasses().keySet()
                                .contains(method.getName())&&method.getParameters().length== params.size()).forEach(method->javaMethods.add(method));
                    }catch (Exception e){}
                });
            }
            //TODO comprobar tambien metodos de PluginObject
            for(int paramIndex=0;paramIndex<params.size();paramIndex++){
                String param=params.get(paramIndex);
                if(!param.contains(var))continue;
                if(param.equals(var)){
                    for(Method javaMethod:javaMethods){
                        predictedTypes.add(mainPlugin.getCodeUtils().checkCustomType(javaMethod.getParameterTypes()[paramIndex].getTypeName()));
                    }
                }else if(param.matches("^([^.(]*)"+var+"\\.(.+)$")){
                    predictTypeOfExecutableVar(plugin,param,var,types);
                }
                predictTypeInParams(plugin,param,var,types);
            }
        }
        updatePredictedTypes(types,predictedTypes);
    }
    private static void predictTypesInBooleanExpression(Plugin plugin,String instruction,String var,Set<String> types,Set<String> vars){
        String content=instruction.replaceAll("^([^(]*)\\s*\\((.+)\\)\\s*\\{(.+)$","$2");
        for(String booleanInst:mainPlugin.getLogic().getBooleans(content,new String[]{" and "," or ","(",")"})){
            if(booleanInst.trim().isEmpty()||!booleanInst.contains(var))continue;
            booleanInst=booleanInst.replace("!","");
            if(booleanInst.equals(var)){types.add(boolean.class.getTypeName());return;}
            String[] operatedElements=mainPlugin.getCodeExecuter().getElements(booleanInst,new String[]{"<",">","="});
            if(operatedElements.length==2){
                String operator=booleanInst.replaceAll(operatedElements[0]+"\\s*(.+)\\s*"+operatedElements[1],"$1");
                if(Arrays.stream(operatedElements).anyMatch(operated->operated.equals(var))){
                    if((operator.contains("<")||operator.contains(">"))){types.add("double");break;}
                    String equality=operatedElements[0].equals(var)?operatedElements[1]:operatedElements[0];
                    String equalityType=checkVarIsNotExecutable(plugin,equality,vars);
                    if(!equality.equals("null")&&equalityType!=null)types.add(equalityType);
                }
                Arrays.stream(operatedElements).forEach(operated->{
                    if(operated.trim().startsWith(var+"."))predictTypeOfExecutableVar(plugin,operated,var,types);
                });
            }else if(booleanInst.matches("^"+var+"\\.(.+)$"))predictTypeOfExecutableVar(plugin,booleanInst,var,types);
        }
    }
    private static void predictTypesOfVarEquality(Plugin plugin,String instruction,String var,Set<String> types,Set<String> vars){
        String equality=instruction.replaceAll("^\\s*"+var+"\\s*=(.*)$","$1");
        if(equality.equalsIgnoreCase("null"))return;
        String type=checkVarIsNotExecutable(plugin,equality,vars);
        if(type!=null){types.add(type);return;}
        Set<String> equalityPredictedTypes=new HashSet<>();
        String[] methods=mainPlugin.getCodeExecuter().getMethodsOfInstruction(equality);
        if(methods.length<2)return;
        String lastMethod=methods[methods.length-1].replaceAll("^([^(]+)\\((.*)\\)$","$1");
        if(methods[methods.length-1].endsWith(")"))lastMethod+="()";
        PluginMethod pluginMethod=mainPlugin.getMethod(lastMethod);
        for(PluginObject object:plugin.getAllObjects()){
            String methodProperty=checkObjectProperties(object,methods[methods.length-1]);
            //TODO solucionar el problema de armor=player.inventory.armor comprobando el anterior metodo (inventory) si es de object o no
            //TODO en caso que sea lo anterior una variable, comprobar si el tipo de la variable es de ese object o no
            //if(methodProperty!=null)equalityPredictedTypes.add(predictTypeOfObjectProperty(object,methodProperty));
            String methodFunction=checkObjectFunctions(object,methods[methods.length-1],lastMethod);
            if(methodFunction!=null)equalityPredictedTypes.add(predictReturnType(object,methodFunction));
        }
        if(pluginMethod==null){updatePredictedTypes(types,equalityPredictedTypes);return;}
        for(String method:pluginMethod.getTranslatedMethodClasses().keySet()){
            for(String classType:pluginMethod.getTranslatedMethodClasses().get(method)){
                equalityPredictedTypes.add(getReturnTypeOfMethod(method,classType));
            }
        }
        updatePredictedTypes(types,equalityPredictedTypes);
    }
    private static void predictTypeOfExecutableVar(Plugin plugin,String instruction,String var,Set<String> types){
        String[] methods=mainPlugin.getCodeExecuter().getMethodsOfInstruction(instruction
                .replaceAll("^([^.(]*)"+var+"\\.(.+)$",var+".$2"));
        if(methods.length<2)return;
        Set<String> executionPredictedTypes=new HashSet<>();
        int varMethods=mainPlugin.getCodeExecuter().getMethodsOfInstruction(var).length;
        String firstMethod=methods[varMethods].replaceAll("^([^(]+)\\((.*)\\)$","$1");
        if(methods[varMethods].endsWith(")"))firstMethod+="()";
        PluginMethod pluginMethod=mainPlugin.getMethod(firstMethod);
        for(PluginObject object:plugin.getAllObjects()){
            String methodProperty=checkObjectProperties(object,methods[varMethods]);
            String methodFunction=checkObjectFunctions(object,methods[varMethods],firstMethod);
            if(methodProperty!=null||methodFunction!=null)executionPredictedTypes.add("PluginObject."+object.getName());
        }
        if(pluginMethod==null){updatePredictedTypes(types,executionPredictedTypes);return;}
        executionPredictedTypes.addAll(pluginMethod.getTranslatedMethodClasses().values().stream().flatMap(list->list.stream())
                .collect(Collectors.toSet()));
        updatePredictedTypes(types,executionPredictedTypes);
    }
    private static void updatePredictedTypes(Set<String> types,Set<String> predictedTypes){
        if(types.isEmpty())types.addAll(predictedTypes);
        else if(!predictedTypes.isEmpty())types.removeIf(classtype->!predictedTypes.contains(classtype));
    }
    private static String checkObjectProperties(PluginObject object,String method){
        for(String property:object.getProperties())if(method.equals(property))return property;
        return null;
    }
    private static String checkObjectFunctions(PluginObject object,String method,String methodName){
        for(String function:object.getFunctions()){
            String functionName=function.replaceAll("^([^(])\\((.+)$","$1");
            List<String> fParams=mainPlugin.getCodeExecuter()
                    .getStringParameters(function.replaceAll("^([^{])\\{(.+)$","$1"));
            List<String> mParams=mainPlugin.getCodeExecuter().getStringParameters(method);
            if(fParams.size()==mParams.size()&&functionName.equals(methodName))return function;
        }
        return null;
    }
    private static String checkVarIsNotExecutable(Plugin plugin,String instruction,Set<String> vars) {
        try {
            Double.parseDouble(instruction);
            try{
                Integer.parseInt(instruction);
                return int.class.getTypeName();
            }catch (Exception e){return double.class.getTypeName();}
        }catch (Exception e){}
        if(instruction.equalsIgnoreCase("true")||instruction.equalsIgnoreCase("false"))return boolean.class.getTypeName();
        if(instruction.startsWith("[")&&instruction.endsWith("]"))return List.class.getTypeName();
        if(instruction.startsWith("{")&&instruction.endsWith("}"))return Map.class.getTypeName();
        String constructor=instruction.replaceAll("^([a-zA-Z]+)\\((.*)$","$1");
        if(mainPlugin.getConstructorTranslator().containsKey(constructor)){
            return mainPlugin.getConstructorTranslator().get(constructor).getTypeName();
        }
        for(PluginObject object:plugin.getObjects()){
            if(constructor.equals(object.getName()))return "PluginObject."+object.getName();
        }
        String var=mainPlugin.getCodeExecuter().getMethodsOfInstruction(instruction)[0];
        if(!vars.contains(var))return String.class.getTypeName();
        return null;
    }
    public static String predictReturnType(PluginObject object, String function){
        Set<String> vars=new HashSet<>();
        for(String property:object.getProperties())vars.add(property);
        vars.addAll(mainPlugin.getCodeExecuter().getStringParameters(function.replaceAll("^([^{]+)\\{(.+)$","$1")));
        String returnInst=getReturnInstruction(function);
        if(returnInst==null)return void.class.getTypeName();
        String executedType=checkVarIsNotExecutable(object.getPlugin(),returnInst,new HashSet<>());
        if(!executedType.equals(String.class.getTypeName()))return executedType;
        //si llega aquí, returnInst es una variable
        if(object.getProperties().contains(returnInst))return predictTypeOfObjectProperty(object,returnInst);
        return predictTypeOfVar(object.getPlugin(),returnInst,function,vars);
    }

    private static String getReturnInstruction(String function){
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            if(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)){
                String returned=getReturnInstruction(instruction);
                if(returned!=null)return returned;
            }else if(instruction.startsWith("return ")){
                String returned=instruction.replace("return ","");
                if(!returned.toLowerCase().trim().equals("null"))return returned;
            }
        }
        return null;
    }
    public static String predictTypeOfObjectProperty(PluginObject object,String property){
        List<String> functions=new ArrayList<>(object.getFunctions());
        functions.addAll(object.getConstructors());
        String equality=object.getPropertyEqualities().get(property);
        Set<String> types=new HashSet<>();
        Set<String> commonVars=new HashSet<>(mainPlugin.getPluginVars(object.getPlugin()).keySet());
        if(equality!=null){
            predictTypesOfVarEquality(object.getPlugin(),property+"="+equality,property,types,commonVars);
            if(!types.isEmpty()){
                Set<String> objectVars=new HashSet<>(commonVars);
                objectVars.addAll(object.getProperties());
                types.add(checkContainerPrediction(new ArrayList<>(types).get(0),object.getPlugin(),functions,property,objectVars));
            }
        }else types.addAll(predictTypeOfVar(object.getPlugin(),property,functions,new HashSet<>(commonVars)));
        BiFunction<String,List<String>,Void> checkPropertyUsage=(func,initialVars)->{
            Set<String> functionVars=new HashSet<>(commonVars);
            functionVars.addAll(initialVars);
            checkObjectPropertiesInFunction(object,property,func,types,functionVars);
            return null;
        };
        if(types.size()!=1){//comprobar tipos posibles en la totalidad del plugin
            //objetos del plugin
            object.getPlugin().getAllObjects().stream().filter(obj->!obj.getName().equals(object.getName())).forEach(obj->{
                Set<String> objectVars=new HashSet<>(commonVars);
                objectVars.addAll(obj.getProperties());
                Function<String,Void> checkFunctionUsageInObject=(func)->{
                    List<String> functionVars=new ArrayList<>(obj.getProperties());
                    functionVars.addAll(mainPlugin.getCodeExecuter().getStringParameters(func.replaceAll("^([^{]+)\\{(.+)$","$1")));
                    checkPropertyUsage.apply(func,functionVars);
                    return null;
                };
                obj.getFunctions().stream().forEach(func->checkFunctionUsageInObject.apply(func));
                obj.getConstructors().stream().forEach(func-> checkFunctionUsageInObject.apply(func));});
            //comandos
            object.getPlugin().getCommands().stream().forEach(command ->checkPropertyUsage.apply(command.getFunction(),new ArrayList<>(command.getCommandVars().keySet())));
            //eventos
            object.getPlugin().getListener().stream().forEach(event ->checkPropertyUsage.apply(event,Arrays.asList("event")));
            if(types.size()!=1)return Object.class.getTypeName();
        }
        return types.stream().findFirst().get();
    }
    private static void checkObjectPropertiesInFunction(PluginObject object,String property,String function,Set<String> types,Set<String> vars){
        List<String> objectVars=new ArrayList<>();
        if(!object.getName().equals(object.getPlugin().getMainObject().getName()))objectVars=getVarsOfObjectInFunction(object.getPlugin(),object.getName(),function);
        else objectVars.add("plugin");
        for(String var:objectVars)predictTypeOfVar(object.getPlugin(),var+"."+property,function,types,vars);
    }
    private static String getReturnTypeOfMethod(String method,String classType){
        String type=Object.class.getTypeName();
        try{
            type = Class.forName(classType).getMethod(method).getReturnType().getTypeName();
        }catch (Exception e){
            try{
                Optional<Method> javaMethod=Arrays.stream(Class.forName(classType).getMethods()).filter(m->m.getName().equals(method)).findFirst();
                if(javaMethod.isPresent())type=javaMethod.get().getReturnType().getTypeName();
            }catch (Exception e2){}
        }
        return mainPlugin.getCodeUtils().checkCustomType(type);
    }
    private static String predictTypeOfContainer(Plugin plugin,List<String> functions,String var,String container,boolean key,Set<String> vars){
        String containerType=Object.class.getTypeName();
        for(String function:functions){
            Set<String> functionVars=new HashSet<>(vars);
            for(String param:mainPlugin.getCodeExecuter().getStringParameters(function.replaceAll("^([^{]+)\\{(.+)$","$1")))
                if(param.matches("^([A-Za-z0-9_]+)$"))functionVars.add(param);
            String type=predictTypeOfContainer(plugin,function,var,container,key,functionVars);
            if(containerType.equals(Object.class.getTypeName())){containerType=type;continue;}
            if(containerType.equals(type)||type.equals(Object.class.getTypeName()))continue;
            if(containerType.equals(Integer.class.getTypeName())&&type.equals(Double.class.getTypeName()))containerType=type;
            else if(!(containerType.equals(Double.class.getTypeName())&&type.equals(Integer.class.getTypeName())))return Object.class.getTypeName();
        }
        return containerType;
    }
    public static String predictTypeOfContainer(Plugin plugin,String function,String var,String container,boolean key,Set<String> vars){
        String containerType=Object.class.getTypeName();
        String methodToFind=container.equals("Map")?"put":"add";
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            if(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)){
                String type=predictTypeOfContainer(plugin,instruction,var,container,key,vars);
                if(containerType.equals(Object.class.getTypeName())||containerType.equals("int")&&type.equals("double"))containerType=type;
                else if(!containerType.equals(type)&&!(containerType.equals("double")&&type.equals("int")))containerType=Object.class.getTypeName();
                continue;
            }
            String type="";
            if(!instruction.trim().startsWith(var+"."+methodToFind))continue;
            List<String> params=mainPlugin.getCodeExecuter().getStringParameters(instruction.trim().replace(var+"."+methodToFind,""));
            if(!methodToFind.equals("put")){
                type=predictContainerParamType(plugin,function,params.get(0),vars);
            }else{
                if(key)type= predictContainerParamType(plugin,function,params.get(0),vars);
                else type= predictContainerParamType(plugin,function,params.get(1),vars);
            }
            if(containerType.equals(Object.class.getTypeName())||containerType.equals("int")&&type.equals("double"))containerType=type;
            else if(!containerType.equals(type)&&!(containerType.equals("double")&&type.equals("int")))containerType=Object.class.getTypeName();
        }
        if(containerType.equals("int"))containerType=Integer.class.getTypeName();
        else if(containerType.equals("double"))containerType=Double.class.getTypeName();
        return containerType;
    }
    private static List<String> getVarsOfObjectInFunction(Plugin plugin, String objectName, String function){
        List<String> objectVars=new ArrayList<>();
        PluginObject object=plugin.getObject(objectName);
        if(object==null)return objectVars;
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            if(mainPlugin.getCodeExecuter().instructionIsFunction(instruction)){
                objectVars.addAll(getVarsOfObjectInFunction(plugin,objectName,instruction));continue;
            }
            if(!instruction.matches("^\\s*([^=]+)\\s*=\\s*" + objectName + "(\\(.*\\))?$"))continue;
            String[] varObject=instruction.replaceAll("^\\s*([^=]+)\\s*=\\s*" + objectName + "(\\(.*\\))?$","$1|"+objectName+"$2").split("\\|");
            String varObjectName=varObject[0];String objectConstructor=varObject[1];
            if(object.getConstructors().isEmpty()&&objectConstructor.equals(objectName)){objectVars.add(varObjectName);continue;}
            List<String> params=mainPlugin.getCodeExecuter().getStringParameters(objectConstructor);
            if(object.getConstructors().stream().anyMatch(constructor->{
                String constructorParams=instruction.replaceAll("^([a-zA-Z_]+)\\(([^{]*)\\)\\{(.+)$","($2)");
                return mainPlugin.getCodeExecuter().getStringParameters(constructorParams).size()==params.size();
            })){objectVars.add(varObjectName);}

        }
        return objectVars;
    }
    private static String predictContainerParamType(Plugin plugin, String function, String param,Set<String> vars){
        String type=checkVarIsNotExecutable(plugin,param,vars);
        if(type!=null&&!type.equals(String.class.getTypeName()))return type;
        String[] methods=mainPlugin.getCodeExecuter().getMethodsOfInstruction(param);
        String varType=predictTypeOfVar(plugin,methods[0],function,vars);
        if(methods.length>1)type=mainPlugin.getCodeUtils().getTypeOfExecution(plugin,param,new HashMap<String,String>(){{put(methods[0],varType);}});
        else type=varType;
        return type;
    }
}
