    package berty.plugincoder.generator.grammar.minigame;
    import org.bukkit.Bukkit;
    import org.bukkit.plugin.java.JavaPlugin;
    import berty.plugincoder.generator.grammar.Priority;
    import berty.plugincoder.generator.grammar.plugin.PluginGrammar;
    import berty.plugincoder.interpreter.executer.CodeExecuter;
    import berty.plugincoder.main.PluginCoder;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    @Priority(value=1)
    public class TeamGrammar {

        protected static int teamsNumber=0;
        protected static List<Integer> teamsCreatedInEachIteration=new ArrayList<>();
        protected static Integer teamsResetTaskId;

        @Priority(value=0)
        public static void add(Map<String,String> data, int iteration){
            TeamGrammar.teamsNumber++;
            if(data.get("TEAMS")==null)TeamGrammar.teamsCreatedInEachIteration.add(1);
            String activationFunction= PluginGrammar.plugin.getActivationFunction();
            activationFunction=activationFunction.substring(0,activationFunction.length()-1);
            activationFunction+="gametemplate.teams.create(team"+teamsNumber+");";
            PluginGrammar.plugin.getActivationContainer().set(0,activationFunction+"}");
            if(teamsResetTaskId==null){
                teamsResetTaskId=Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(PluginCoder.class),()->{
                    teamsNumber=0;teamsResetTaskId=null;teamsCreatedInEachIteration.clear();
                },2);
            }
        }
        @Priority(value=1)
        public static void name(Map<String,String> data,int iteration){
            updateTeamCreates(data.get("NAME"),iteration,"^(.+)\\.teams.create\\((.+)\\)$","$1.teams.create(",")");
        }
        @Priority(value=1)
        public static void names(Map<String,String> data,int iteration){
            named(data,iteration);
        }
        @Priority(value=1)
        public static void named(Map<String,String> data,int iteration){
            updateTeamCreates(getContentText(data,"NAME","TEXT"),iteration,"^(.+)\\.teams.create\\((.+)\\)$","$1.teams.create(",")");
        }
        @Priority(value=2)
        public static void players(Map<String,String> data,int iteration){
            updateTeamCreates(getContentText(data,"PLAYERS","NUMBER"),iteration,"^(.+)\\.teams.create\\((.+)\\)$","$1.teams.create($2,",")");
        }
        @Priority(value=3)
        public static void color(Map<String,String> data,int iteration){
            updateTeamCreates(data.get("COLOR"),iteration,"^(.+)\\.teams.create\\((.+)\\)$","$1.teams.create($2,",")");
        }
        @Priority(value=3)
        public static void colors(Map<String,String> data,int iteration){
            updateTeamCreates(getContentText(data,"COLOR","COLOR"),iteration,"^(.+)\\.teams.create\\((.+)\\)$","$1.teams.create($2,",")");
        }
        private static String getContentText(Map<String,String> data,String varName,String dataType){
            String contentText=data.get(varName);
            if(contentText==null){
                contentText="";
                for(String contentKey:data.keySet().stream().filter(key->key.startsWith(dataType+"_")).sorted().collect(Collectors.toList())){
                    contentText+=(contentText.isEmpty()?"":",")+data.get(contentKey);
                }
            }
            return contentText;
        }
        private static void updateTeamCreates(String contentText,int iterationIndex,String regrex,String replaceRegrexPrefix,String replaceRegrexSuffix){
            String[] content=contentText.split(",");
            String activationFunction="Activation{";
            CodeExecuter executer=JavaPlugin.getPlugin(PluginCoder.class).getCodeExecuter();
            List<String> instructions=executer.getInstructionsFromFunction(PluginGrammar.plugin.getActivationFunction());
            int contentIndex=0;int matchesIndex=0;int startContentIndex=0;
            if(content.length==1){
                String newContent=content[0];
                for(int i=0;i<teamsCreatedInEachIteration.get(iterationIndex)-1;i++)newContent+=","+content[0];
                content=newContent.split(",");
            }
            for(int iteration=0;iteration<iterationIndex;iteration++){
                startContentIndex+=teamsCreatedInEachIteration.get(iteration);
            }
            for(String instruction:instructions){
                if(instruction.matches("^(.+)\\.teams.create\\((.+)\\)$")){
                    if(startContentIndex<=matchesIndex){
                        instruction=instruction.replaceAll(regrex,replaceRegrexPrefix+content[contentIndex]+replaceRegrexSuffix);
                        contentIndex++;
                    }
                    matchesIndex++;
                }
                activationFunction+=instruction+(executer.instructionIsFunction(instruction)?"":";");
            }
            PluginGrammar.plugin.getActivationContainer().set(0,activationFunction+"}");
        }
    }
