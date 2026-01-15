package berty.plugincoder.generator.grammar;

import java.util.Map;

public class Action {

    private String name;
    private Map<String,String> data;
    private int iteration;
    public Action(String name, Map<String,String> data,int iteration){
        this.name=name;this.data=data;this.iteration=iteration;
    }
    public void execute(Class grammar){
        try {
            grammar.getMethod(name, Map.class,int.class).invoke(null,data,iteration);
        } catch (Exception e) {
            //error
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
