package berty.plugincoder.generator.grammar.command;

import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.generator.grammar.plugin.PluginGrammar;
import java.util.HashMap;
import java.util.Map;

@Priority(value = 1)
public class CommandGrammar {

    @Priority(value = 0)
    public static void add(Map<String,String> data, int iteration){
        PluginGrammar.plugin.addCommand("",new HashMap<>(),"");
    }
    @Priority(value = 1)
    public static void prompt(Map<String,String> data, int iteration){
        String prompt=data.get("PROMPT");
        PluginGrammar.plugin.getCommands().get(iteration).setPrompt(prompt);
    }
}
