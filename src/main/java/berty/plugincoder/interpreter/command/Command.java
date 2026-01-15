package berty.plugincoder.interpreter.command;

import berty.plugincoder.interpreter.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command {

    private String prompt="";
    private List<String> functionContainer=new ArrayList<>();
    private Map<String,CommandVarType> commandVars=new HashMap<>();
    public Command(Plugin plugin){
        functionContainer.add("command"+plugin.getCommands().size()+"{}");
        plugin.getCommands().add(this);
    }
    public String getPrompt() {
        return prompt;
    }
    public List<String> getFunctionContainer() {
        return functionContainer;
    }
    public String getFunction() {
        return functionContainer.get(0);
    }

    public Map<String, CommandVarType> getCommandVars() {
        return commandVars;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setFunctionContent(String functionContent) {
        if(functionContent.trim().isEmpty())return;
        String newFunction=functionContainer.get(0).replaceAll("^([^{]+)\\{(.*)\\}$","$1{"+functionContent+"}");
        functionContainer.set(0,newFunction);
    }

    @Override
    public String toString(){
        return "Command[prompt="+prompt+",vars="+commandVars.toString()+",function="+getFunction()+"]";
    }
}
