package berty.plugincoder.generator.grammar.event;

import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.generator.grammar.function.FunctionGrammar;
import berty.plugincoder.generator.grammar.plugin.PluginGrammar;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Priority(value = 1)
public class EventGrammar{

    @Priority(value = 0)
    public static void add(Map<String,String> data, int iteration){

    }
    @Priority(value = 1)
    public static void to(Map<String,String> data, int iteration){
        if(data.get("EVENT")!=null){//to detect
            List<String> eventData= Arrays.asList(data.get("EVENT").split("-"));
            String event=eventData.get(0)+"{";
            if(eventData.contains("player"))event+="player=event.player;";
            PluginGrammar.plugin.getListener().add(event+"}");
            return;
        }
        FunctionGrammar.functionContainers.add(PluginGrammar.plugin.getListener()); //to execute
    }
}
