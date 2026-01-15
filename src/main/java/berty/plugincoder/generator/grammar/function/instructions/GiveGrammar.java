package berty.plugincoder.generator.grammar.function.instructions;

import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.generator.grammar.function.FunctionGrammar;

import java.util.Map;

@Priority(value = 3)
public class GiveGrammar {

    public static String givenTag;
    @Priority(value = 0)
    public static void to(Map<String,String> data, int iteration){
        String function= FunctionGrammar.functionContainers.get(iteration).get(iteration);
        function=function.substring(0,function.length()-1);
        if(data.containsKey("PLAYER")&&givenTag.equals("ITEM"))function+="player.inventory.add(item);";
        FunctionGrammar.functionContainers.get(iteration).set(iteration,function+"}");
    }
}
