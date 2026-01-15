package berty.plugincoder.generator.grammar.function.instructions;

import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.generator.grammar.function.FunctionGrammar;

import java.util.Map;

@Priority(value = 3)
public class SendGrammar {
    @Priority(value = 0)
    public static void to(Map<String,String> data, int iteration){
        String function= FunctionGrammar.functionContainers.get(iteration).get(iteration);
        String sender=data.containsKey("PLAYER")?"player":"server.console";
        function=function.replace("sender.",sender+".");
        FunctionGrammar.functionContainers.get(iteration).set(iteration,function);
    }
}
