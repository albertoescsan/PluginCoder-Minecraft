package berty.plugincoder.compiler;

import berty.plugincoder.compiler.classes.scoreboard.ScoreboardFile;
import berty.plugincoder.interpreter.classes.scoreboard.Scoreboard;
import berty.plugincoder.writer.PluginFileWriter;
import berty.plugincoder.interpreter.command.Command;
import berty.plugincoder.interpreter.command.CommandVarType;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.GUI.InicVars;
import berty.plugincoder.predictor.PredictType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class PluginJavaTranslator {

    public static PluginCoder mainPlugin= JavaPlugin.getPlugin(PluginCoder.class);
    private static boolean isCreatingMainClass=false;

    private static boolean insideRepeat=false;
    private static Set<String> repeatVariables=new HashSet<>();

    //conjunto para comprobar si hay variables numericas for en la instruccion de task, ya que hay que ponerle el final
    private static Set<String> forNumVars=new HashSet<>();
    private static Set<String> finalVars=new HashSet<>();
    public static void createJavaPlugin(String filesPrefix,Plugin plugin) throws IOException {
        new File(filesPrefix).mkdirs();
        isCreatingMainClass=true;
        new File(filesPrefix+"/main").mkdirs();
        isCreatingMainClass=false;
        new File(filesPrefix+"/listener").mkdirs();
        new File(filesPrefix+"/commands").mkdirs();
        new File(filesPrefix+"/objects").mkdirs();
        createMainClass(filesPrefix,plugin);
        createListener(filesPrefix,plugin);
        createObjects(filesPrefix,plugin);
        createCommands(filesPrefix,plugin);
        repeatVariables.clear();
    }
    public static void createPluginYML(String filesPrefix, Plugin plugin) throws IOException {
        File pluginYML=new File(filesPrefix+"/plugin.yml");
        String content="name: "+plugin.getName()+"\nversion: "+plugin.getMainObject().getPropertyEqualities().get("version")
                +"\nmain: main.Plugin\napi-version: 1."+mainPlugin.getVersionNumber();
        Set<String> commands=new HashSet<>();
        for(Command command: plugin.getCommands())commands.add(command.getPrompt().split(" ")[0]);
        content+="\n"+"commands:";
        for(String command: commands) content+="\n    "+command+":";
        pluginYML.createNewFile();
        PluginFileWriter.writeInFile(pluginYML,content);
    }
    private static void createMainClass(String filesPrefix, Plugin plugin) throws IOException {
        InicVars.functionType="PluginObject.Plugin";
        Set<String> typesImported=new HashSet<>();
        String imports="package main;\n\nimport org.bukkit.plugin.java.JavaPlugin;\nimport listener.PluginListener;";
        String startContent = "\npublic class Plugin extends JavaPlugin{";
        startContent+="\npublic void onEnable(){";
        startContent+="\nthis.getServer().getPluginManager().registerEvents(new PluginListener(this),this);";
        Set<String> commands=new HashSet<>();
        for(Command command: plugin.getCommands())commands.add(command.getPrompt().split(" ")[0]);
        for(String command: commands){
            String commandName=String.valueOf(command.charAt(0)).toUpperCase()+command.substring(1,command.length());
            startContent+="\nthis.getCommand(\""+command+"\").setExecutor(new "+commandName+"Command(this));";
            importType(typesImported,"commands."+commandName+"Command");
        }
        Map<String,String> inicVarTypes=mainPlugin.getPluginVarTypes();
        startContent+=getFunctionTranslation(plugin,plugin.getActivationContainer().get(0),new HashMap<>(inicVarTypes),typesImported);
        startContent+="\n}";
        startContent+="\npublic void onDisable(){";
        startContent+=getFunctionTranslation(plugin,plugin.getDeactivationContainer().get(0),new HashMap<>(inicVarTypes),typesImported);
        startContent+="\n}";
        Map<String,String> varTypes=InicVars.getInicVarTypes(plugin,"");
        String content=getObjectClassContent(plugin.getMainObject(),varTypes,typesImported);
        content+="\n}";
        File mainClass = new File(filesPrefix + "/main/Plugin.java");
        mainClass.createNewFile();
        imports+=importAllTypes(typesImported);
        for(String var:repeatVariables)startContent+="\n"+var;
        PluginFileWriter.writeInFile(mainClass,imports+"\n"+startContent+"\n"+content);
    }

    private static void createListener(String filesPrefix, Plugin plugin) throws IOException {
        repeatVariables.clear();
        InicVars.functionType="event";
        String imports="package listener;\n\nimport org.bukkit.event.Listener;\nimport org.bukkit.event.EventHandler;\nimport main.Plugin;";
        String startContent = "\npublic class PluginListener implements Listener{\n\nprivate Plugin plugin;\n\npublic PluginListener(Plugin plugin){\n" +
                "this.plugin=plugin;\n}";
        String content="";
        Map<String,Integer> eventsIndex=new HashMap<>();
        Set<String> typesImported=new HashSet<>();
        for(String eventFunction:plugin.getListener()){
            Set<String> actualRepeatVars=new HashSet<>(repeatVariables);
            String eventName=eventFunction.replaceAll("^([^{]+)\\{(.+)$","$1");
            content+="\n\n@EventHandler";
            Integer eventIndex=eventsIndex.get(eventName);
            Map<String,String> variableTypes=InicVars.getInicVarTypes(plugin,eventFunction);
            String eventType=variableTypes.get("event");
            importType(typesImported,eventType);
            if(eventIndex==null)eventIndex=0;
            content+="\npublic void "+eventName+(eventIndex!=0?eventIndex:"")+"("+eventType.replaceAll("^(.+)\\.([^.]+$)","$2")+" event){";
            eventsIndex.put(eventName,eventIndex+1);
            content+=getFunctionTranslation(plugin,eventFunction,variableTypes,typesImported);
            content+="\n}";
            for(String var:repeatVariables){
                if(actualRepeatVars.contains(var))continue;
                content=content.replace(var.replace("private ",""),var.split(" ")[2]);
            }
        }
        content+="\n\n}";
        File listener = new File(filesPrefix + "/listener/PluginListener.java");
        listener.createNewFile();
        imports+=importAllTypes(typesImported);
        for(String var:repeatVariables)startContent+="\n"+var+";";
        PluginFileWriter.writeInFile(listener,imports+"\n"+startContent+"\n"+content);
    }
    private static void createCommands(String filesPrefix, Plugin plugin) throws IOException {
        InicVars.functionType="command";
        List<String> translatedCommands=new ArrayList<>();
        for(Command command:plugin.getCommands()){
            String arg0=command.getPrompt().split(" ")[0];
            if(translatedCommands.contains(arg0))continue;
            repeatVariables.clear();
            translatedCommands.add(arg0);
            String commandName=String.valueOf(arg0.charAt(0)).toUpperCase()+arg0.substring(1,arg0.length());
            File commandFile = new File(filesPrefix + "/commands/"+commandName+"Command"+".java");
            if(!commandFile.exists())commandFile.createNewFile();
            Set<String> typesImported=new HashSet<>();
            String imports="package commands;\n\nimport main.Plugin;\nimport org.bukkit.command.Command;\n" +
                    "import org.bukkit.command.CommandExecutor;\n" +
                    "import org.bukkit.command.CommandSender;";
            String startContent="\npublic class "+commandName+"Command implements CommandExecutor{";
            String content="\nprivate Plugin plugin;"+
                    "\n\npublic "+commandName+"Command(Plugin plugin){this.plugin=plugin;}"+
            "\n\n@Override\npublic boolean onCommand(CommandSender sender, Command command, String label, String[] args){\n";
            Map<Integer,List<Command>> commandArgs=new HashMap<>();
            for(Command c:plugin.getCommands()){
                if(!c.getPrompt().startsWith(arg0+" ")&&!c.getPrompt().equals(arg0))continue;
                int argsNumber=c.getPrompt().split(" ").length-1;
                if(commandArgs.get(argsNumber)==null)commandArgs.put(argsNumber,new ArrayList<>());
                commandArgs.get(argsNumber).add(c);
            }
            for(int argsNum=0;argsNum<=commandArgs.keySet().stream().mapToInt(n->n).max().orElse(0);argsNum++){
                if(commandArgs.get(argsNum)==null)continue;
                content+="\nif(args.length=="+argsNum+"){";
                for(Command c:commandArgs.get(argsNum)){
                    String condContent="\nif(";
                    String functionContent="";
                    String[] args=c.getPrompt().split(" ");
                    Map<String,String> varTypes=InicVars.getInicVarTypes(plugin,c.getFunction());
                    for(int argIndex=0;argIndex<args.length-1;argIndex++){
                        String var=args[argIndex+1];
                        if(c.getCommandVars().get(var)==null){
                            condContent+="args["+argIndex+"].equalsIgnoreCase(\""+var+"\")&&";
                            continue;
                        }
                        if(c.getCommandVars().get(var)== CommandVarType.PLAYER){
                            functionContent+="\n"+getTypeName(Player.class.getTypeName(),typesImported)+" "+
                                    var+"=Bukkit.getPlayer(args["+argIndex+"]);";
                            functionContent+="\nif("+var+"==null)return true;";
                            importType(typesImported,Bukkit.class.getTypeName());
                            importType(typesImported,Player.class.getTypeName());
                        }else if(c.getCommandVars().get(var)== CommandVarType.NUMBER){
                            functionContent+="\ndouble "+var+"=0";
                            functionContent+="\ntry{";
                            functionContent+="\n"+var+"=Double.parseDouble(args["+argIndex+"]);\"";
                            functionContent+="\n}catch(Exception e){return true;}";
                            importType(typesImported,Exception.class.getTypeName());
                        }else if(c.getCommandVars().get(var)== CommandVarType.TEXT){
                            functionContent+="\nString "+var+"=args["+argIndex+"];";
                        }else functionContent+="\nboolean "+var+"=args["+argIndex+"].equalsIgnoreCase(\"true\");";
                    }
                    if(condContent.endsWith("&&"))condContent=condContent.substring(0,condContent.length()-2);
                    condContent+="){";
                    if(condContent.equals("\nif(){"))condContent="";
                    functionContent+=getFunctionTranslation(plugin,c.getFunction(),varTypes,typesImported);
                    content+=condContent+functionContent+(condContent.isEmpty()?"":"\n}");
                }
                content+="\n}";
                for(String var:repeatVariables){
                    content=content.replace(var.replace("private ",""),var.split(" ")[2]);
                }
            }
            content+="\nreturn true;";
            content+="\n}";
            content+="\n}";
            imports+=importAllTypes(typesImported);
            for(String var:repeatVariables)startContent+="\n"+var+";";
            PluginFileWriter.writeInFile(commandFile,imports+"\n"+startContent+"\n"+content);
        }
    }
    private static void createObjects(String filesPrefix, Plugin plugin) throws IOException {
        for(PluginObject object:plugin.getObjects()){
            repeatVariables.clear();
            InicVars.functionType="PluginObject."+object.getName();
            Map<String,String> varTypes=InicVars.getInicVarTypes(object.getPlugin(),"");
            String imports="package objects;\n\nimport main.Plugin;";
            Set<String> typesImported=new HashSet<>();
            String startContent="\n\npublic class "+object.getName();
            if(object.getParent()!=null)startContent+=" extends "+object.getParent().getName();
            startContent+="{\n\nprivate Plugin plugin=JavaPlugin.getPlugin(Plugin.class);";
            importType(typesImported,JavaPlugin.class.getTypeName());
            String content=getObjectClassContent(object,varTypes,typesImported);
            content+="\n}";
            imports+=importAllTypes(typesImported);
            File objectFile = new File(filesPrefix + "/objects/"+object.getName()+".java");
            objectFile.createNewFile();
            for(String var:repeatVariables){
                content=content.replace(var.replace("private ",""),var.split(" ")[2]);
            }
            for(String var:repeatVariables)startContent+="\n"+var+";";
            PluginFileWriter.writeInFile(objectFile,imports+"\n"+startContent+"\n"+content);
        }

    }
    private static String getObjectClassContent(PluginObject object,Map<String,String> varTypes,Set<String> typesImported){
        String content="";
        //traducir propiedades
        for(String property: object.getDeclaredProperties()){
            if(property.equals("name")&&object.equals(object.getPlugin().getMainObject()))continue;
            String propertyType=checkCointainerType(varTypes.get(property),typesImported);
            String equality=object.getPropertyEqualities().get(property);
            if(equality!=null){
                equality=getInstructionTranslation(object.getPlugin(),equality,varTypes,typesImported);
                equality=equality.replaceAll("^(.*)plugin(.*)$","$1JavaPlugin.getPlugin(Plugin.class)$2");
                content+="\nprivate "+propertyType+" "+property+"="+equality+";";
            }else content+="\nprivate "+propertyType+" "+property+";";
        }
        //traducir constructores
        if(!object.getName().equalsIgnoreCase("plugin"))
            content+="\n"+getObjectFunctionTranslation(object,object.getConstructors(),true,varTypes,typesImported);
        //traducir funciones
        content+="\n"+getObjectFunctionTranslation(object,object.getDeclaredFunctions(),false,varTypes,typesImported);
        //traducir getters and setters
        for(String property: object.getDeclaredProperties()){
            if(property.equals("name")&&object.equals(object.getPlugin().getMainObject()))continue;
            String type=checkCointainerType(varTypes.get(property),typesImported);
            String methodPropName=String.valueOf(property.charAt(0)).toUpperCase()+property.substring(1,property.length());
            content+="\npublic "+type+" get"+methodPropName+"(){";
            content+="\nreturn "+property+";";
            content+="\n}";
            content+="\npublic void set"+methodPropName+"("+type+" "+property+"){";
            content+="\nthis."+property+"="+property+";";
            content+="\n}";
        }
        return content;
    }
    private static String checkCointainerType(String type,Set<String> typesImported){
        if(type.startsWith("List.")||type.startsWith("Set.")){
            String container=type.replaceAll("^([^.]+)\\.(.+)$","$1");
            String containerType=type.replaceAll("^([^.]+)\\.(.+)$","$2");
            importType(typesImported,mainPlugin.getCodeUtils().clearContainerType(type));
            importType(typesImported,containerType);
            return container+"<"+getTypeName(containerType,typesImported)+">";
        }else if(type.startsWith("Map.")){
            importType(typesImported,Map.class.getTypeName());
            String keyType=type.replaceAll("^([^.]+)\\.(.+)-(.+)$","$2");
            String valueType=type.replaceAll("^([^.]+)\\.(.+)-(.+)$","$3");
            importType(typesImported,keyType);
            importType(typesImported,valueType);
            return "Map<"+getTypeName(keyType,typesImported)+","+getTypeName(valueType,typesImported)+">";
        }else importType(typesImported,type);
        return getTypeName(type,typesImported);
    }
    private static String getObjectFunctionTranslation(PluginObject object,List<String> functions,
                                                       boolean constructor,Map<String,String> varTypes, Set<String> typesImported){
        String content="";
        for(String function: functions){
            String functionName=function.matches("^([a-zA-Z0-9_]+)\\{(.+)\\}$")?
                    function.replaceAll("^([a-zA-Z0-9_]+)\\{(.+)$","$1"):
                    function.replaceAll("^([a-zA-Z0-9_]+)\\((.+)$","$1");
            String paramsText=function.replaceAll("^"+functionName+"\\(([^{]+)\\)\\{(.+)$","($1)");
            List<String> functionParams=mainPlugin.getCodeExecuter().getStringParameters(paramsText);
            String returnType= PredictType.predictReturnType(object,function);
            String params="(";
            Set<String> predictParams=new HashSet<>(object.getProperties());
            predictParams.addAll(functionParams);
            for(String param:functionParams){
                String paramType=PredictType.predictTypeOfVar(object.getPlugin(),param,function,predictParams);
                varTypes.put(param,paramType);
                params+=getTypeName(paramType,typesImported)+" "+param+",";
                importType(typesImported,paramType);
            }
            if(params.endsWith(","))params=params.substring(0,params.length()-1);
            params+=")";
            if(constructor)content+="\npublic "+object.getName()+params+"{";
            else content+="\npublic "+getTypeName(returnType,typesImported)+" "+functionName+params+"{";
            importType(typesImported,returnType);
            content+=getFunctionTranslation(object.getPlugin(),function,varTypes,typesImported);
            content+="\n}";
        }
        return content;
    }
    private static String getFunctionTranslation(Plugin plugin,String function,Map<String,String> variableTypes,Set<String> typesImported){
        String content="";
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            instruction=instruction.trim();
            if(!mainPlugin.getCodeExecuter().instructionIsFunction(instruction)){
                if(instruction.matches("^([^({=]+)=(.+)$")){//instruccion variable
                    String varName=instruction.replaceAll("^([^({=]+)=(.+)$","$1");
                    String varValue=instruction.replaceAll("^([^({=]+)=(.+)$","$2");
                    String varType=variableTypes.get(varValue);
                    boolean newVar=!variableTypes.containsKey(varName);
                    if(varType==null)varType=mainPlugin.getCodeUtils().getTypeOfExecution(plugin,varValue,variableTypes);
                    String container="";
                    String typeName=getTypeName(varType,typesImported);
                    if(varType.startsWith("List.")){
                        container="List";
                        importType(typesImported,List.class.getTypeName());
                        varType=varType.replaceAll("^List\\.(.*)$","$1");
                        if(varType.isEmpty())varType=PredictType.predictTypeOfContainer(plugin,function,varName,container,false,new HashSet<>(variableTypes.keySet()));
                        typeName=getTypeName(varType,typesImported);

                    }else if(varType.startsWith("Set.")){
                        container="Set";
                        importType(typesImported,Set.class.getTypeName());
                        varType=varType.replaceAll("^Set\\.(.*)$","$1");
                        if(varType.isEmpty())varType=PredictType.predictTypeOfContainer(plugin,function,varName,container,false,new HashSet<>(variableTypes.keySet()));
                        typeName=getTypeName(varType,typesImported);

                    }else if(varType.startsWith("Map.")){
                        container="Map";
                        importType(typesImported,Map.class.getTypeName());
                        varType=varType.replaceAll("^Map\\.(.*)$","$1");
                        if(varType.isEmpty()){
                            String keyType=PredictType.predictTypeOfContainer(plugin,function,varName,container,true,new HashSet<>(variableTypes.keySet()));
                            String valueype=PredictType.predictTypeOfContainer(plugin,function,varName,container,false,new HashSet<>(variableTypes.keySet()));
                            varType=keyType+"-"+valueype;
                        }
                        String[] mapSeparatedTypes=varType.split("-");
                        typeName= getTypeName(mapSeparatedTypes[0],typesImported)+","+getTypeName(mapSeparatedTypes[1],typesImported);
                    }
                    for(String typeToImport:varType.split("-")) importType(typesImported,typeToImport);
                    String varValueTranslated=getInstructionTranslation(plugin,varValue,variableTypes,typesImported);
                    content+="\n"+(container.isEmpty()?(newVar?typeName+" ":""):container+"<"+typeName+"> ")+varName+"="+varValueTranslated+";";
                    if(newVar){
                        if(container.isEmpty()) variableTypes.put(varName,varType);
                        else  variableTypes.put(varName,container+"."+varType);
                    }else if(insideRepeat){
                        varType=variableTypes.get(varName);
                        typeName=getTypeName(varType,typesImported);
                        repeatVariables.add("private "+typeName+" "+varName);
                    }
                }else{//instruccion ejecutable
                    if(instruction.startsWith("return ")){
                        content+="\nreturn "+getInstructionTranslation(plugin,instruction.replace("return ",""),variableTypes,typesImported)+";";
                    }else content+="\n"+getInstructionTranslation(plugin,instruction,variableTypes,typesImported)+";";
                }
            }else{
                Map<String,String> instructionVariableTypes=new HashMap<>(variableTypes);
                if(instruction.matches("^\\s*if\\s*\\((.*)$")||instruction.matches("^\\s*else\\s*if\\s*\\((.*)$")){
                    String condition=instruction.replaceAll("^\\s*(else )?\\s*if\\s*\\(([^{}]*)\\)\\s*\\{(.+)$","$2");
                    condition=getInstructionTranslation(plugin,condition,variableTypes,typesImported);
                    content+="\n"+(instruction.startsWith("else ")?"else if(":"if(")+condition+"){"+
                            getFunctionTranslation(plugin,instruction,instructionVariableTypes,typesImported)+"\n}";
                }else if(instruction.matches("^\\s*else\\s*\\{(.+)$")){
                    content+="\nelse{"+getFunctionTranslation(plugin,instruction,instructionVariableTypes,typesImported)+"\n}";
                }else if(instruction.matches("\\s*for\\s*\\((.*)$")){
                    boolean isNumFor=true;
                    String forVar=instruction.replaceAll("^for\\(([^{:]+):([^{]+)\\)\\s*\\{(.+)$","$1");
                    if(instruction.matches("^\\s*for\\s*\\(([^{:]+):([^{]+)->([^{:]+)(:([^{]+))?\\)\\s*\\{(.+)$")){
                        //number for increment
                        content+=translateNumberedForFunction(plugin,instruction,forVar,"->",instructionVariableTypes,typesImported);
                    }else if(instruction.matches("^\\s*for\\s*\\(([^{:]+)\\:([^{]+)<-([^{]+)\\)\\s*\\{(.+)$")){
                        //number for decrement
                        content+=translateNumberedForFunction(plugin,instruction,forVar,"<-",instructionVariableTypes,typesImported);
                    }else{
                        //iterator for
                        isNumFor=false;
                        String iterable=instruction.replaceAll("^\\s*for\\s*\\(([^{:]+)\\:([^{]+)\\)\\s*\\{(.+)$","$2");
                        String forVarType=mainPlugin.getCodeUtils().getTypeOfExecution(plugin,iterable,variableTypes).replaceAll("^([^.]+)\\.(.+)$","$2");
                        String forVarTypeName=getTypeName(forVarType,typesImported);
                        iterable=getInstructionTranslation(plugin,iterable,variableTypes,typesImported);
                        if(forVarTypeName.equals("Integer"))forVarTypeName="int";
                        else if(forVarTypeName.equals("Double"))forVarTypeName="double";
                        else if(forVarTypeName.equals("Boolean"))forVarTypeName="boolean";
                        instructionVariableTypes.put(forVar,forVarType);
                        content+="\nfor("+forVarTypeName+" "+forVar+":"+iterable+"){";
                        importType(typesImported,forVarType);
                    }
                    if(isNumFor)forNumVars.add(forVar);
                    content+=getFunctionTranslation(plugin,instruction,instructionVariableTypes,typesImported)+"\n}";
                    forNumVars.remove(forVar);
                }else if(instruction.matches("\\s*while\\s*\\((.*)$")){
                    String condition=instruction.replaceAll("^\\s*while\\s*\\(([^)]*)\\)\\s*\\{(.+)$","$1");
                    condition=getInstructionTranslation(plugin,condition,variableTypes,typesImported);
                    content+="\nwhile("+condition+"){\n"+
                            getFunctionTranslation(plugin,instruction,instructionVariableTypes,typesImported)+"\n}";
                }else if(instruction.matches("^\\s*delay\\s*\\((.*)$")||instruction.matches("^\\s*repeat\\s*\\((.*)$")){
                    String taskContent="";
                    Optional<String> forNumVarOpt=forNumVars.stream().filter(v->variableTypes.containsKey(v)).findFirst();
                    if(forNumVarOpt.isPresent()){
                        String oldVarName=forNumVarOpt.get();
                        finalVars.add(oldVarName);
                        taskContent=getFunctionTranslation(plugin,instruction,instructionVariableTypes,typesImported);
                        finalVars.remove(oldVarName);
                        String newVarName="final"+forNumVarOpt.get().toUpperCase();
                        content+="\nfinal "+variableTypes.get(oldVarName)+" "+newVarName+"="+oldVarName+";";
                    }
                    if(instruction.trim().startsWith("delay")){
                        String seconds=instruction.replaceAll("^delay\\(([^{]+)\\)\\s*\\{(.*)}$","$1");
                        content+="\nBukkit.getScheduler().scheduleSyncDelayedTask("+(isCreatingMainClass?"this":"plugin")+", ()-> {";
                        content+=taskContent.isEmpty()?getFunctionTranslation(plugin,instruction,instructionVariableTypes,typesImported):taskContent;
                        content+="\n},(long)(("+seconds+")*20));";
                        importType(typesImported,Bukkit.class.getTypeName());
                    }else{
                        insideRepeat=true;
                        List<String> secondParams=mainPlugin.getCodeExecuter()
                                .getStringParameters(instruction.replaceAll("^([^{]+)\\{(.*)}$","$1"));
                        content+="\nnew BukkitRunnable() {\npublic void run() {";
                        content+=getFunctionTranslation(plugin,instruction,instructionVariableTypes,typesImported);
                        content+="\n}}.runTaskTimer("+(isCreatingMainClass?"this":"plugin")+","
                                +(secondParams.size()==1?"0":"(long)(("+secondParams.get(0)+")*20)")+",(long)(("+secondParams.get(secondParams.size()-1)+")*20));";
                        importType(typesImported,BukkitRunnable.class.getTypeName());
                        insideRepeat=false;
                    }
                }
            }
        }
        return content;
    }
    private static String getInstructionTranslation(Plugin plugin,String instruction,Map<String, String> variableTypes,Set<String> typesImported){
        if(instruction.trim().equals("stop"))return "break";
        else if(instruction.trim().equals("continue"))return "continue";
        else if(!insideRepeat&&instruction.startsWith("cancel(")){
            String translatedTaskId=getFunctionTranslation(plugin,instruction.replaceAll("^cancel\\((.+)\\)$","$1"),variableTypes,typesImported);
            importType(typesImported,Bukkit.class.getTypeName());
            return "Bukkit.getScheduler().cancelTask("+translatedTaskId+")";
        }
        else if(instruction.trim().equals("cancel"))return "cancel()";
        else if(instruction.trim().isEmpty())return "\""+instruction+"\"";
        String varValueTranslated="";
        Map<String,Object> vars=new HashMap<>(variableTypes);
        vars.putAll(mainPlugin.getPluginVars(plugin));
        String instructionType= mainPlugin.getCodeExecuter().getVarInstructionType(instruction,vars);
        if(instructionType.equals("String")){
            boolean hasColor=false;
            for(String color:mainPlugin.getColorTranslator().keySet()){
                if(!instruction.contains(color+"+")||instruction.contains("_"+color+"+"))continue;hasColor=true;
                String colorTranslation=mainPlugin.getColorTranslator().get(color);
                instruction=instruction.replace(color+"+","\""+colorTranslation+"\"+");
            }
            for(String text:mainPlugin.getCodeExecuter().getElements(instruction,new String[]{"+","(",")"})){
                if(text.startsWith("\"&"))continue;
                String translatedText=getInstructionTranslation(plugin,text,variableTypes,typesImported);
                String[] methods=mainPlugin.getCodeExecuter().getMethodsOfInstruction(text);
                if(!variableTypes.containsKey(methods[0])){
                    instruction.replace(text,"\""+translatedText+"\"");
                }else instruction.replace(text,translatedText);
                instruction=instruction.replace(text,translatedText);
            }
            if(hasColor) {
                varValueTranslated="ChatColor.translateAlternateColorCodes('&',"+instruction+")";
                importType(typesImported,ChatColor.class.getTypeName());
            }
            else varValueTranslated=instruction;
        }else if(instructionType.equals("Math")){
            for(String number:mainPlugin.getCodeExecuter().getElements(instruction,new String[]{"+","-","*","/","%","(",")"})){
                String translatedNumber=getInstructionTranslation(plugin,number,variableTypes,typesImported);
                instruction=instruction.replace(number,translatedNumber);
            }
            varValueTranslated=instruction;
        }else if(instructionType.equals("Boolean")){
            String[] booleans=mainPlugin.getCodeExecuter().getElements(instruction,new String[]{" and "," or ","(",")"});
            for(String bool:booleans){
                String translatedBool="";
                if(booleans.length>1){
                    translatedBool=getInstructionTranslation(plugin,bool,variableTypes,typesImported);
                }else if(bool.matches("^([^{]+) is (\\w+)")){
                    String executionBool=bool.replaceAll("^([^{]+) is (\\w+)","$1");
                    String classInstance=bool.replaceAll("^([^{]+) is (\\w+)","$2");
                    translatedBool+=getInstructionTranslation(plugin,executionBool,variableTypes,typesImported);
                    translatedBool+=" instanceof "+classInstance;
                    if(classInstance.equals("Player")) importType(typesImported,Player.class.getTypeName());
                    else if(classInstance.equals("Entity")) importType(typesImported,Entity.class.getTypeName());
                    else importType(typesImported,"PluginObject."+classInstance);//objeto del plugin
                }else if(instruction.matches("^([^{]+)([<>=(<=)(>=)])([^{=]+)")){
                    String[] elements=mainPlugin.getCodeExecuter().getElements(instruction,new String[]{"<",">","="});
                    try{
                        String element1=elements[0];
                        String element2=elements[1];
                        String operator=instruction.replaceAll(element1+"(.+)"+element2,"$1");
                        String element1Type=mainPlugin.getCodeUtils().getTypeOfExecution(plugin,element1,variableTypes);
                        element1=getInstructionTranslation(plugin,element1,variableTypes,typesImported);
                        element2=getInstructionTranslation(plugin,element2,variableTypes,typesImported);
                        if(operator.equals("=")){
                            if(element1Type.equals("int")||element1Type.equals("double")||
                                    element1Type.equals("float")||element1Type.equals("long")){
                                translatedBool=element1+"=="+element2;
                            }else translatedBool=element1+".equals("+element2+")";
                        }else translatedBool=element1+operator+element2;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                instruction=instruction.replace(bool,translatedBool);
            }
            instruction=instruction.replace(" and ","&&");
            instruction=instruction.replace(" or ","||");
            varValueTranslated=instruction;
        }else if(instruction.startsWith("[")&&instruction.endsWith("]")){
            String translatedElements="";
            for(String listElement:mainPlugin.getCodeExecuter().getStringParameters("("+instruction.substring(1,instruction.length()-1)+")")){
                translatedElements+=getInstructionTranslation(plugin,listElement,variableTypes,typesImported)+",";
            }
            if(translatedElements.isEmpty())varValueTranslated="new ArrayList<>()";
            else {
                translatedElements=translatedElements.substring(0,translatedElements.length()-1);
                String listType= mainPlugin.getCodeUtils().getTypeOfExecution(plugin,instruction,variableTypes);
                String typeName=getTypeName(listType.replace("List.",""),typesImported);
                varValueTranslated="Arrays.asList(new "+typeName+"[]{"+translatedElements+"})";
                importType(typesImported,Arrays.class.getTypeName());
            }
            importType(typesImported,ArrayList.class.getTypeName());

        }else if(instruction.startsWith("{")&&instruction.endsWith("}")){
            String translatedElements="";
            for(String keyValue:mainPlugin.getCodeExecuter().getStringParameters("("+instruction.substring(1,instruction.length()-1)+")")){
                String[] keyValueContainer=mainPlugin.getCodeUtils().getElementsBySeparator(keyValue,':');
                String translatedKey=getInstructionTranslation(plugin,keyValueContainer[0],variableTypes,typesImported);
                String translatedValue=getInstructionTranslation(plugin,keyValueContainer[1],variableTypes,typesImported);
                translatedElements+="put("+translatedKey+","+translatedValue+");";
            }
            if(translatedElements.isEmpty())varValueTranslated="new HashMap<>()";
            else{
                String mapTypes= mainPlugin.getCodeUtils().getTypeOfExecution(plugin,instruction,variableTypes);
                String[] typesName=mapTypes.replace("Map.","").split("-");
                String keyTypeName= getTypeName(typesName[0],typesImported);
                String valueTypeName= getTypeName(typesName[1],typesImported);
                varValueTranslated="new HashMap<"+keyTypeName+","+valueTypeName+">() {{"+translatedElements+"}}";
            }
            importType(typesImported,HashMap.class.getTypeName());

        }else{
            //Comprobar si es constructor
            String constructorName=instruction.replaceAll("^([^(]+)\\((.+)$","$1");
            Class constructorClass=mainPlugin.getConstructorTranslator().get(constructorName);
            String constructorParams="";
            if(constructorClass!=null){
                List<String> params=mainPlugin.getCodeExecuter().getStringParameters(instruction);
                if(constructorClass.equals(Inventory.class)){
                    for(int i=params.size()-1;i>=0;i--){
                        constructorParams+=getInstructionTranslation(plugin,params.get(i),variableTypes,typesImported)+",";
                    }
                    if(!constructorParams.isEmpty())constructorParams=constructorParams.substring(0,constructorParams.length()-1);
                    varValueTranslated="Bukkit.createInventory(null,9*"+constructorParams+")";
                    importType(typesImported,Bukkit.class.getTypeName());
                }else{// traduccion de constructor generica
                    List<Object> paramTypes=new ArrayList<>();
                    for(int i=0;i<params.size();i++){
                        String type= mainPlugin.getCodeUtils().clearContainerType(mainPlugin.getCodeUtils().getTypeOfExecution(plugin,params.get(i),variableTypes));
                        paramTypes.add(type);
                        constructorParams+=getInstructionTranslation(plugin,params.get(i),variableTypes,typesImported)+",";
                    }
                    if(!constructorParams.isEmpty())constructorParams=constructorParams.substring(0,constructorParams.length()-1);
                    Constructor constructor=null;
                    for(Constructor c:constructorClass.getConstructors()){
                        if(c.getParameterTypes().length!=paramTypes.size())continue;
                        if(mainPlugin.getCodeExecuter().validateParams(c.getParameterTypes(),paramTypes,false)){constructor=c;break;}
                    }
                    if(constructor!=null){
                        for(int paramIndex=0;paramIndex<constructor.getParameterTypes().length;paramIndex++){
                            Class constParamType=constructor.getParameterTypes()[paramIndex];
                            if(!constParamType.isEnum())continue;
                            String param=params.get(paramIndex);
                            if(variableTypes.get(param)!=null &&variableTypes.get(param).equals(String.class.getTypeName())){
                                String translatedParam=getTypeName(constParamType.getTypeName(),typesImported)+".valueOf("+param+")";
                                constructorParams=constructorParams.replace(param,translatedParam);
                            }else{
                                String translatedParam=getTypeName(constParamType.getTypeName(),typesImported)+"."+param;
                                constructorParams=constructorParams.replace("\""+param+"\"",translatedParam);
                            }
                            importType(typesImported,constParamType.getTypeName());
                        }
                    }
                    varValueTranslated="new "+getTypeName(constructorClass.getTypeName(),typesImported)+"("+constructorParams+")";
                    //comprobar clases internas de PluginCoder para añadirlas al código
                    if(constructorClass.getTypeName().equals(Scoreboard.class.getTypeName()))ScoreboardFile.createFile(plugin);
                }
                importType(typesImported,constructorClass.getTypeName());
                return varValueTranslated;
            }else{//comprobar constructor de objeto
                PluginObject object=plugin.getObject(constructorName);
                if(object!=null){
                    List<String> params=mainPlugin.getCodeExecuter().getStringParameters(instruction);
                    for(int i=0;i<params.size();i++)constructorParams+=getInstructionTranslation(plugin,params.get(i),variableTypes,typesImported)+",";
                    if(!constructorParams.isEmpty())constructorParams=constructorParams.substring(0,constructorParams.length()-1);
                    varValueTranslated="new "+object.getName()+"("+constructorParams+")";
                    importType(typesImported,"PluginObject."+object.getName());
                    return varValueTranslated;
                }
            }
            //comprobar que no sea un numero
            try {
                Double.parseDouble(instruction);
                return instruction;
            }catch (Exception e){}
            String[] methods=mainPlugin.getCodeExecuter().getMethodsOfInstruction(instruction);
            if(variableTypes.containsKey(methods[0])){
                if(variableTypes.get(methods[0])!=null&&variableTypes.get(methods[0]).equals(Bukkit.getServer().getClass().getTypeName())){
                    varValueTranslated="Bukkit.getServer()";
                    importType(typesImported,Bukkit.class.getTypeName());
                }else {
                    if(finalVars.contains(methods[0]))varValueTranslated="final"+methods[0].toUpperCase();
                    else varValueTranslated=methods[0];
                }
            }else{
                if(instruction.equals("null")||instruction.equals("true")||instruction.equals("false"))varValueTranslated=instruction;
                else varValueTranslated="\""+instruction+"\"";
                return varValueTranslated;
            }
            String executionType= mainPlugin.getCodeUtils().clearContainerType(variableTypes.get(methods[0]));
            List<String> executionTypes=Arrays.asList(executionType);
            for(int i=1;i<methods.length;i++){
                Map<String,List<String>> methodTypes=mainPlugin.getCodeUtils().getMethodsOfType(executionType);
                String methodName=methods[i].replaceAll("^([^(]+)\\((.+)\\)$","$1");
                String params="";
                List<Object> paramTypes=new ArrayList<>();
                List<String> paramsList=mainPlugin.getCodeExecuter().getStringParameters(methods[i]);
                if(methodName.length()!=methods[i].length()){
                    methodName+="()";
                    for(String param:paramsList){
                        String type= mainPlugin.getCodeUtils().clearContainerType(mainPlugin.getCodeUtils().getTypeOfExecution(plugin,param,variableTypes));
                        paramTypes.add(type);
                        params+=getInstructionTranslation(plugin,param,variableTypes,typesImported)+",";
                    }
                    params=params.substring(0,params.length()-1);//quitarle la última coma
                }
                Method javaMethod=mainPlugin.getCodeUtils().getJavaMethod(executionTypes,methodName,paramTypes);
                //ver si es un metodo custom y si es así adaptarlo, como teleport o message
                if(javaMethod!=null){
                    if(javaMethod.getName().equalsIgnoreCase("sendMessage")&&!paramTypes.get(0).equals(String.class.getTypeName())){
                        if(!params.startsWith("\"")&&!params.startsWith("ChatColor."))params="String.valueOf("+params+")";
                    }else if(javaMethod.getName().equalsIgnoreCase("teleport")&&!paramTypes.get(0).equals(Location.class.getTypeName())){
                        if(mainPlugin.getCodeExecuter().getStringParameters("("+params+")").size()!=3)params="new Location("+params+")";
                        else params="new Location("+varValueTranslated+".getWorld(),"+params+")";
                        importType(typesImported,Location.class.getTypeName());
                    }else{
                        for(int paramIndex=0;paramIndex<paramsList.size();paramIndex++){
                            Class methodParamType=javaMethod.getParameterTypes()[paramIndex];
                            if(!methodParamType.isEnum())continue;
                            String param=paramsList.get(paramIndex);
                            if(variableTypes.get(param)!=null &&variableTypes.get(param).equals(String.class.getTypeName())){
                                String translatedParam=getTypeName(methodParamType.getTypeName(),typesImported)+".valueOf("+param+")";
                                params=params.replace(param,translatedParam);
                            }
                            else {
                                String translatedParam=getTypeName(methodParamType.getTypeName(),typesImported)+"."+param;
                                params=params.replace("\""+param+"\"",translatedParam);
                            }
                            importType(typesImported,methodParamType.getTypeName());
                        }
                    }
                    executionType=mainPlugin.getCodeUtils().checkCustomType(javaMethod.getReturnType().getTypeName());
                    //comprobar si la clase corresponde a una clase interna de PluginCoder
                    if(executionType.equals(Scoreboard.class.getTypeName())){ //player.getScoreboard()
                        varValueTranslated="Scoreboard.getScoreboard("+varValueTranslated+"."+javaMethod.getName()+"("+params+"))";
                        ScoreboardFile.createFile(plugin);
                        importType(typesImported,"objects.Scoreboard");
                    }else if(javaMethod.getName().equals("setScoreboard")){//player.setScoreboard(scoreboard)
                        varValueTranslated+="."+javaMethod.getName()+"("+params+".getBukkitScoreboard())";
                        ScoreboardFile.createFile(plugin);
                    }else varValueTranslated+="."+javaMethod.getName()+"("+params+")";

                }else{//metodo de objeto
                    String methodRawName=methodName.replace("()","");
                    PluginObject object=plugin.getObject(executionType.replace("PluginObject.",""));
                    if(object!=null&&object.getProperties().contains(methodRawName)){
                        methodRawName=String.valueOf(methodRawName.charAt(0)).toUpperCase()
                                +methodRawName.substring(1,methodRawName.length());
                        if(params.isEmpty())methodRawName="get"+methodRawName;
                        else methodRawName="set"+methodRawName;
                    }
                    varValueTranslated+="."+methodRawName+"("+params+")";
                    executionType=methodTypes.get(methodName).get(0);
                }
                executionTypes=methodTypes.get(methodName);
            }
        }
        return varValueTranslated;
    }

    private static String translateNumberedForFunction(Plugin plugin,String forFunction,String forVar,String flecha, Map<String, String> variableTypes,Set<String> typesImported){
        String num1=forFunction.replaceAll("^for\\(([^{:]+):([^{]+)"+flecha+"([^{:]+)(:([^{]+))?\\)\\{(.+)$","$2");
        String num2=forFunction.replaceAll("^for\\(([^{:]+):([^{]+)"+flecha+"([^{:]+)(:([^{]+))?\\)\\{(.+)$","$3");
        if(flecha.equals("<-")){String n=num1;num1=num2;num2=n;}
        String num1Type=mainPlugin.getCodeUtils().getTypeOfExecution(plugin,num1,variableTypes);
        String num2Type=mainPlugin.getCodeUtils().getTypeOfExecution(plugin,num2,variableTypes);
        String incType="int";
        num1=getInstructionTranslation(plugin,num1,variableTypes,typesImported);
        num2=getInstructionTranslation(plugin,num2,variableTypes,typesImported);
        String increment="1";
        String operator="<=";
        if(forFunction.matches("^for\\(([^{:]+):([^{]+)"+flecha+"([^{:]+):([^{]+)\\)\\{(.+)$")){
            increment=forFunction.replaceAll("^for\\(([^{:]+):([^{]+)"+flecha+"([^{:]+):([^{]+)\\)\\{(.+)$","$4");
            incType=mainPlugin.getCodeUtils().getTypeOfExecution(plugin,increment,variableTypes);
            increment=getInstructionTranslation(plugin,increment,variableTypes,typesImported);
        }
        String forVarType=incType.equals("int")&&num1Type.equals("int")?"int":"double";
        //ver si son numeros tal cual y cambiar el orden y el incrementador
        boolean someNumIsAnExecution=true;
        boolean incrUpdate=false;
        try {
            double num1Value=Double.parseDouble(num1);
            double num2Value=Double.parseDouble(num2);
            double incrementValue=Double.parseDouble(increment);
            someNumIsAnExecution=false;
            if(num1Value>num2Value){
                if(incrementValue>0){
                    increment="-"+increment;incrUpdate=true;
                }
                operator=">=";
            }
        }catch (Exception e){}
        //comprobar iterador (si no ha cambiado todavía)
        try {
            double incrementValue=Double.parseDouble(increment);
            if(incrementValue<0&&!incrUpdate)increment=((incrementValue*-1)+"").replaceAll("\\.0$","");
        }catch (Exception e){}
        String forContent="";
        if(someNumIsAnExecution){
            forContent="\n"+num1Type+" forStartVar="+num1+";"+num2Type+" forEndVar="+num2+";\n"+incType+" forIncVar="+increment+";int forIncSign=1;" +
                    "\nif(forStartVar>forEndVar)forIncSign=-1;\nif(forIncVar<0)forIncSign*=-1;\nforIncVar*=forIncSign;" +
                    "\nfor("+forVarType+" "+forVar+"=forStartVar;forIncVar>=0?"+forVar+"<=forEndVar:"+forVar+">=forEndVar;"+forVar+"+=forIncVar){";
        }else{
            forContent+="\nfor("+forVarType+" "+forVar+"="+num1+";"+forVar+operator+num2+";"+forVar+"+="+increment+"){";
        }
        variableTypes.put(forVar,forVarType.equals("int")?int.class.getTypeName():double.class.getTypeName());
        return forContent;
    }
    private static String getTypeName(String type,Set<String> typesImported){
        final String mappedType=mapImportedType(type);
        String typeName=mappedType.replaceAll("^(.+)\\.([^.]+)$","$2");
        if(typesImported.stream().map(imported->mapImportedType(imported)).anyMatch(imported->imported.endsWith("."+typeName)&&!imported.equals(mappedType)))return mappedType;
        return typeName;
    }
    private static void importType(Set<String> typesImported, String type){
        if(!getTypeName(type,typesImported).equals(type))typesImported.add(type);
    }
    private static String importAllTypes(Set<String> typesImported) {
        String imports="";
        Set<String> typesMapedToImport=new HashSet<>();
        typesImported.stream().map(imported->{
            if(imported.endsWith("[]"))imported=imported.substring(0,imported.length()-2);
            return mapImportedType(imported);
        }).forEach(imported->typesMapedToImport.add(imported));
        for(String typeImported:typesMapedToImport){
            if(typeImported.equals(String.class.getTypeName())|| typeImported.equals(int.class.getTypeName())
            ||typeImported.equals(float.class.getTypeName())||typeImported.equals(long.class.getTypeName())||
            typeImported.equals(double.class.getTypeName())||typeImported.equals(boolean.class.getTypeName())||
            typeImported.equals(void.class.getTypeName()))continue;
            imports+="\nimport "+typeImported+";";
        }
        return imports;
    }
    private static String mapImportedType(String imported){
        if(imported.startsWith("berty.plugincoder.interpreter.classes")||imported.startsWith("PluginObject.")){
            String typeName=imported.replaceAll("^(.+)\\.([^.]+)$","$2");
            return "objects."+typeName;
        }
        return imported;
    }
}
