package berty.plugincoder.generator.grammar.function;

import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.generator.grammar.function.instructions.GiveGrammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Priority(value = 2)
public class FunctionGrammar {

    public static List<List<String>> functionContainers=new ArrayList<>();

    @Priority(value = 0)
    public static void send(Map<String,String> data, int iteration){
        String function=functionContainers.get(iteration).get(iteration);
        function=function.substring(0,function.length()-1);
        if(data.containsKey("MESSAGE")){
            function+="sender.message("+data.get("TEXT")+");";
        }else{
            //title
        }
        functionContainers.get(iteration).set(iteration,function+"}");

    }
    @Priority(value = 0)
    public static void open(Map<String,String> data, int iteration){

    }
    @Priority(value = 0)
    public static void give(Map<String,String> data, int iteration){
        String function=functionContainers.get(iteration).get(iteration);
        function=function.substring(0,function.length()-1);
        if(data.containsKey("MATERIAL")){
            String amount=data.get("NUMBER")!=null?","+data.get("NUMBER"):"";
            function+="item=ItemStack("+data.get("MATERIAL")+amount+");";
            GiveGrammar.givenTag="ITEM";
        }
        functionContainers.get(iteration).set(iteration,function+"}");
    }
}
