package berty.plugincoder.writer;

import berty.plugincoder.interpreter.command.Command;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class PluginFileWriter {

    private PluginCoder mainPlugin;

    public PluginFileWriter(PluginCoder pluginCoder) {
        mainPlugin=pluginCoder;
    }
    public void savePlugin(Plugin plugin){
        File pluginDir=new File(mainPlugin.getDataFolder().getPath()+"/plugins/");
        if(!pluginDir.exists())pluginDir.mkdirs();
        File pluginFile=new File(mainPlugin.getDataFolder().getPath()+"/plugins/"+plugin.getName()+".txt");
        try{
            if(!pluginFile.exists())pluginFile.createNewFile();
        }catch (Exception e){return;}
        String content="Activation{\n";
        content+=getInstructionContents(plugin.getActivationContainer().get(0));
        content+="}\n";
        content+="Deactivation{\n";
        content+=getInstructionContents(plugin.getDeactivationContainer().get(0));
        content+="}\n";
        content+="Listener{\n";
        for(String event:plugin.getListener()){
            content+=getFunctionContent(event)+"\n";
        }
        content+="}\n";
        content+="Commands{\n";
        for(Command command:plugin.getCommands()){
            content+="command{\n";
            content+="prompt={/"+command.getPrompt()+"}\n";
            content+="vars={";
            for(String var:command.getCommandVars().keySet())content+=var+"="+command.getCommandVars().get(var)+",";
            if(content.endsWith(","))content=content.substring(0,content.length()-1);
            content+="}\n";
            content+="function={\n";
            content+=getInstructionContents(command.getFunction());
            content+="}\n";
            content+="}\n";
        }
        content+="}\n";
        content+="Objects{\n";
        content+=getObjectContent(plugin.getMainObject())+"\n";
        for(PluginObject object:plugin.getObjects()){
            content+=getObjectContent(object)+"\n";
        }
        content+="}\n";
        writeInFile(pluginFile,content);
    }
    private String getFunctionContent(String function){
        if(function.trim().isEmpty())return "";
        String functionContent=function.replaceAll("^([^{]+)\\{(.*)$","$1")+"{\n";
        functionContent+=getInstructionContents(function)+"}";
        return functionContent;
    }
    private String getInstructionContents(String function){
        if(function.trim().isEmpty())return "";
        String instructionContent="";
        for(String instruction:mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
            if(mainPlugin.getCodeExecuter().instructionIsFunction(instruction))instructionContent+=getFunctionContent(instruction)+"\n";
            else instructionContent+=instruction+";\n";
        }
        return instructionContent;
    }
    private String getObjectContent(PluginObject object){
        String objectContent="";
        objectContent+=object.getName();
        if(object.getParent()!=null)objectContent+=" from "+object.getParent().getName();
        objectContent+="{\n";
        for(String property:object.getDeclaredProperties().stream().sorted().collect(Collectors.toList())){
            if(property.equals("name")&&object.equals(object.getPlugin().getMainObject()))continue;
            objectContent+=property;
            if(object.getPropertyEqualities().get(property)!=null)objectContent+="="+object.getPropertyEqualities().get(property)+";\n";
            else objectContent+=";\n";
        }
        for(String constructor:object.getConstructors()){
            String constructorName=constructor.replaceAll("^([^{]+)\\{(.*)$","$1");
            constructorName.replace(object.getName().toLowerCase(),object.getName());
            constructor=constructor.replaceAll("^([^{]+)\\{(.*)$",constructorName+"{$2");
            objectContent+=getFunctionContent(constructor)+"\n";
        }
        for(String function:object.getDeclaredFunctions()){
            objectContent+=getFunctionContent(function)+"\n";
        }
        objectContent+="}";
        return objectContent;
    }
    public static void writeInFile(File file, String content){
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
