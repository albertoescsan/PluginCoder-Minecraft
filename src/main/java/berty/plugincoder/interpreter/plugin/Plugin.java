package berty.plugincoder.interpreter.plugin;

import berty.plugincoder.interpreter.command.CommandVarType;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.command.Command;
import berty.plugincoder.interpreter.objects.PluginObjectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Plugin {
    private String name;
    private boolean isEnabled=true;
    private List<String> listener=new ArrayList<>();
    private List<String> activation =new ArrayList<>();
    private List<String> deactivation =new ArrayList<>();
    private PluginObject mainObject;
    private PluginObjectInstance mainObjectInstance;
    private List<PluginObject> objects=new ArrayList<>();
    private List<Command> commands=new ArrayList<>();
    public Plugin(String name){
        this.name=name;
        mainObject=new PluginObject(this,"Plugin");
        mainObject.addProperty("name",name);
        mainObject.addProperty("version","1.0.0");
        updateMainObjectInstance();
    }
    public String getName() {
        return name;
    }

    public PluginObject getObject(String name){
        if(name.equalsIgnoreCase(mainObject.getName()))return mainObject;
        for(PluginObject object:objects){
            if(object.getName().equalsIgnoreCase(name))return object;
        }
        return null;
    }
    public Command addCommand(String prompt, Map<String, CommandVarType> vars, String functionContent){
        Command command=new Command(this);
        command.setPrompt(prompt);
        command.getCommandVars().putAll(vars);
        command.setFunctionContent(functionContent);
        return command;
    }
    public void removeCommand(Command command){
        if(!commands.remove(command))return;
        for(int i=0;i<commands.size();i++){
            Command c=commands.get(i);
            String function=c.getFunction();
            c.getFunctionContainer().clear();
            function=function.replaceAll("^command\\d+\\{(.*)}$","command"+i+"{$1}");
            c.getFunctionContainer().add(function);
        }
    }
    public void setName(String name) {
        this.name = name;
        mainObject.getDeclaredPropertyEqualities().put("name",name);
    }
    public void updateMainObjectInstance(){
        mainObjectInstance=mainObject.getInstance();
    }
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public List<String> getListener() {
        return listener;
    }

    public List<String> getActivationContainer() {return activation;}
    public String getActivationFunction() {return activation.get(0);}

    public List<String> getDeactivationContainer() {
        return deactivation;
    }
    public String getDeactivationFunction() {return deactivation.get(0);}

    public PluginObject getMainObject() {
        return mainObject;
    }

    public PluginObjectInstance getMainObjectInstance() {
        return mainObjectInstance;
    }

    public List<PluginObject> getObjects() {
        return objects;
    }
    public List<PluginObject> getAllObjects() {
        List<PluginObject> objects=new ArrayList<>(this.objects);
        objects.add(mainObject);
        return objects;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
