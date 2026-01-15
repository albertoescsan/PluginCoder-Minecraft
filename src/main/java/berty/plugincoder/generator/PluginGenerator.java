package berty.plugincoder.generator;
import berty.plugincoder.generator.grammar.Action;
import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.generator.grammar.plugin.PluginGrammar;
import berty.plugincoder.interpreter.Language;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.reader.PluginFileReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginGenerator {
    private static PluginCoder pluginCoder= JavaPlugin.getPlugin(PluginCoder.class);

    public static boolean generate(String generatorFile){
        String secuence="";
        for(String line: PluginFileReader.linesFromFile(pluginCoder.getDataFolder().getParentFile().getPath()+"/PluginCoder/generator/"+generatorFile+".txt","UTF-8")){
            secuence+=line.trim()+" ";
        }
        String finalSecuence = secuence;
        Map<Language,Integer> languagePoints=new HashMap<>();
        Map<Language,LangData> languageDatas=new HashMap<>();
        for(Language language:Language.values()){
            LangData langData=new LangData(language);
            languageDatas.put(language,langData);
            AtomicInteger points= new AtomicInteger();
            langData.getActions().stream().forEach(action->{
                if(finalSecuence.contains(action)) points.getAndIncrement();
            });
            languagePoints.put(language,points.get());
        }
        Language language=languagePoints.keySet().stream().sorted(Comparator.comparingInt(lang->-languagePoints.get(lang))).findFirst().orElse(Language.ENGLISH);
        createCode(secuence.trim(),languageDatas.get(language));
        PluginGrammar.plugin.updateMainObjectInstance();
        pluginCoder.getPlugins().add(PluginGrammar.plugin);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+ PluginGrammar.plugin.getName());
        return true;
    }

    public static boolean createCode(String secuence,LangData langData) {
        boolean insideCom=false;
        List<String> textValues=new ArrayList<>();
        String textValue="";
        String secuenceLowerCase="";
        for(char character:secuence.toCharArray()){
            if(character=='"'){
                if(insideCom){textValues.add(textValue.substring(1,textValue.length()));textValue="";}
                insideCom=!insideCom;
            }
            if(!insideCom)secuenceLowerCase+=String.valueOf(character).toLowerCase();
            else {
                secuenceLowerCase+=character;
                textValue+=character;
            }
        }
        secuence=secuenceLowerCase;
        //reemplazar texto
        for(int valueIndex=0;valueIndex<textValues.size();valueIndex++){
            secuence=secuence.replace("\""+textValues.get(valueIndex)+"\"","&text"+valueIndex);
        }
        secuence = secuence.replaceAll("(\\s+)([0-9]+)(\\s+)", "$1NUMBER($2)$3");
        for(String plugin: langData.getPlugins())secuence=secuence.replaceAll("(\\s+)" + plugin + "(\\s|$)", "$1PLUGIN("+plugin+")$2");
        for(String entity: langData.getEntities())secuence=secuence.replaceAll("(\\s+)"+entity+"(\\s|$)","$1ENTITY("+entity+")$2");
        for(String event: langData.getEvents().keySet())secuence=secuence.replaceAll("(\\s+)"+event+"(\\s|$)","$1EVENT("+event+")$2");
        for(String action:langData.getActions())secuence=secuence.replaceAll("(\\s*)"+action+"(\\s|$)","$1ACTION("+action+")$2");
        for(String color:langData.getColors().keySet())secuence=secuence.replaceAll("([\\s,])"+color+"(([\\s,])|$)","$1COLOR("+color+")$2");
        for(String material:langData.getMaterials().keySet())secuence=secuence.replaceAll("([\\s,])"+material+"(([\\s,])|$)","$1MATERIAL("+material+")$2");
        //restaurar texto
        for(int valueIndex=0;valueIndex<textValues.size();valueIndex++){
            secuence=secuence.replace("&text"+valueIndex,"TEXT("+textValues.get(valueIndex)+")");
        }
        Map<Class,List<Action>> grammarActions=new HashMap<>();
        List<String> secuenceArgs=getSecuenceArgs(secuence);
        List<Integer> actionIndexes=new ArrayList<>();
        for(int i=0;i<secuenceArgs.size();i++)if(secuenceArgs.get(i).startsWith("ACTION("))actionIndexes.add(i);
        List<Class> grammarClassSecuence=new ArrayList<>();
        Map<Class, Integer> grammarIterations=new HashMap<>();
        for(int actionIndex:actionIndexes){
            String action=secuenceArgs.get(actionIndex).replaceAll("^ACTION\\((.+)\\)$","$1");
            List<String> grammars= langData.getGrammar().get(action);
            String detectedGrammar="";
            for(String grammar:grammars){
                Map<String,String> actionData=new HashMap<>();int correctGrammarArgsCount=0;
                List<String> grammarArgs=new ArrayList<>(Arrays.asList(grammar.split(" ")));
                String multipleDataType="";
                int actionGrammarIndex=grammarArgs.indexOf(action);
                for(int grammarIndex=0;grammarIndex<grammarArgs.size();grammarIndex++){
                    String grammarArg=grammarArgs.get(grammarIndex);
                    if(!grammarArg.endsWith("..."))continue;
                    multipleDataType=grammarArg.replace("...","");
                    grammarArgs.set(grammarIndex,multipleDataType);
                    int secuenceInitialIndex=actionIndex-actionGrammarIndex+grammarIndex+1;
                    int secuenceFinalIndex=secuenceInitialIndex;
                    for(int secuenceIndex=secuenceInitialIndex;secuenceIndex<secuenceArgs.size();secuenceIndex++){
                        String secuenceDataType=secuenceArgs.get(secuenceIndex).replaceAll("^([A-Z]+)\\((.+)\\)$","$1");
                        if(!secuenceDataType.equals(multipleDataType)){secuenceFinalIndex=secuenceIndex;break;}
                        if(secuenceIndex+1==secuenceArgs.size()){secuenceFinalIndex=secuenceArgs.size();}
                    }
                    if(secuenceFinalIndex-secuenceInitialIndex==0)break;
                    int grammarOldSize=grammarArgs.size();
                    for(int i=0;i<secuenceFinalIndex-secuenceInitialIndex;i++)grammarArgs.add(multipleDataType);
                    for(int i=grammarIndex+1;i<grammarOldSize;i++){
                        String removedArg=grammarArgs.remove(grammarIndex+1);
                        grammarArgs.add(removedArg);
                    }
                    break;
                }
                int multipleDataTypeId=1;
                for(int grammarIndex=0;grammarIndex<grammarArgs.size();grammarIndex++){
                    String[]grammarOptions=grammarArgs.get(grammarIndex).split("/");
                    int optionNotMatchCount=0;
                    for(String grammarOption:grammarOptions){
                        String[] grammarArg=grammarOption.split("_");
                        String grammarArgDataType=grammarArg[0];
                        String grammarArgName=grammarArg[grammarArg.length>1?1:0];
                        if(grammarArgDataType.equals(action))break;
                        String secuenceDataType=secuenceArgs.get(actionIndex-actionGrammarIndex+grammarIndex).replaceAll("^([A-Z]+)\\((.+)\\)$","$1");
                        if(!grammarArgDataType.equals(secuenceDataType)){optionNotMatchCount++;continue;}
                        String secuenceArg=secuenceArgs.get(actionIndex-actionGrammarIndex+grammarIndex).replaceAll("^([A-Z]+)\\((.+)\\)$","$2");
                        if(grammarArgDataType.equals("COLOR"))secuenceArg=langData.getColors().get(secuenceArg);
                        else if(grammarArgDataType.equals("EVENT"))secuenceArg=langData.getEvents().get(secuenceArg);
                        else if(grammarArgDataType.equals("MATERIAL"))secuenceArg=langData.getMaterials().get(secuenceArg);
                        if(grammarArgDataType.equals(multipleDataType)){
                            actionData.put(grammarArgDataType+"_"+multipleDataTypeId,secuenceArg);
                            multipleDataTypeId++;
                        }else if(grammarArgDataType.equals("PLUGIN")||grammarArgDataType.equals("ENTITY")){
                            nextGrammarClass(grammarClassSecuence,secuenceArg,langData,grammarIterations);
                        }
                        else if(!secuenceDataType.equals(secuenceArg)||grammarArg.length>1)actionData.put(grammarArgName,secuenceArg);
                        break;
                    }
                    if(optionNotMatchCount==grammarOptions.length)break;
                    correctGrammarArgsCount++;
                }

                //se encontro una gramática que coincide con la sentencia
                if(correctGrammarArgsCount==grammarArgs.size()){
                    detectedGrammar=grammar;
                    String actionTranslation=langData.getTranslator().get(action);
                    if(actionTranslation==null)actionTranslation=action;
                    Class grammarClass=checkActionClass(actionTranslation,grammarClassSecuence);
                    if(grammarActions.get(grammarClass)==null)grammarActions.put(grammarClass,new ArrayList<>());
                    grammarActions.get(grammarClass).add(new Action(actionTranslation,actionData,grammarIterations.get(grammarClass)));
                    break;
                }
            }
            if(detectedGrammar.isEmpty()){
                //error, no se cumple ninguna gramatica de la acción
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"GRAMMAR ERROR");
                return false;
            }
            String nextGrammar=langData.getActionNextGrammar().get(action);
            if(nextGrammar==null)nextGrammar=langData.getActionNextGrammar().get(detectedGrammar);
            if(nextGrammar!=null) nextGrammarClass(grammarClassSecuence,nextGrammar,langData,grammarIterations);

        }
        //ejecutar actions por cada grammar en el orden adecuado
        grammarActions.keySet().stream().sorted(Comparator.comparingInt(grammar->((Priority)(grammar.getAnnotation(Priority.class))).value())).forEach(grammar->{
            grammarActions.get(grammar).stream().sorted(Comparator.comparing(action ->{
                try {
                    return grammar.getMethod(action.getName(),Map.class,int.class).getAnnotation(Priority.class).value();
                } catch (Exception e) {
                    //error
                    return 0;
                }
            }
            )).forEach(action -> action.execute(grammar));
        });
        return true;
    }
    private static Class checkActionClass(String action, List<Class> grammarClassSecuence){
        if(grammarClassSecuence.size()==0)return null;
        Class grammarClass=grammarClassSecuence.get(grammarClassSecuence.size()-1);
        boolean grammarClassFound=false;
        while(!grammarClassFound){
            try{
                grammarClass.getMethod(action,Map.class,int.class);
                grammarClassFound=true;
            } catch (Exception e) {
                grammarClassSecuence.remove(grammarClassSecuence.size()-1);
                if(grammarClassSecuence.size()==0)return null;
                grammarClass=grammarClassSecuence.get(grammarClassSecuence.size()-1);
            }
        }
        return grammarClass;
    }
    private static List<String> getSecuenceArgs(String secuence){
        List<String> args=new ArrayList<>();
        int parentesisCount=0;String arg="";
        for(char character:secuence.toCharArray()){
            if(character=='(')parentesisCount++;
            else if (character==')')parentesisCount--;
            if(parentesisCount==0&&(character==' '||character==',')){if(!arg.isEmpty())args.add(arg);arg="";}
            else arg+=character;
        }
        if(!arg.isEmpty())args.add(arg.trim());
        return args;
    }
    private static void nextGrammarClass(List<Class>grammarClassSecuence, String secuenceArg, LangData langData, Map<Class,Integer> grammarIterations){
        try {
            String grammarClassInicName=langData.getTranslator().get(secuenceArg);
            if(grammarClassInicName==null)grammarClassInicName=secuenceArg;
            Class grammarClass=grammarClassSecuence.isEmpty()?null:grammarClassSecuence.get(grammarClassSecuence.size()-1);
            if(grammarClass==null||!grammarClass.getSimpleName().toLowerCase().equals(grammarClassInicName+"grammar")) {
                grammarClass=findGrammarClassPath(grammarClassInicName);
                if(!grammarIterations.containsKey(grammarClass))grammarIterations.put(grammarClass,0);
                else grammarIterations.put(grammarClass,grammarIterations.get(grammarClass)+1);
                grammarClassSecuence.add(grammarClass);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static Class findGrammarClassPath(String grammarName) {
        String className=String.valueOf(grammarName.charAt(0)).toUpperCase()+grammarName.substring(1,grammarName.length()).toLowerCase()+"Grammar.class";
        try {
            JarFile jar = new JarFile(new File(
                    PluginCoder.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            ));
            Optional<JarEntry> grammarClass=jar.stream().filter(entry->entry.getName().startsWith("plugincoder/generator/grammar/")
                    &&entry.getName().endsWith(className)).findFirst();
            if(grammarClass.isPresent())return Class.forName(grammarClass.get().getName().replace("/",".").replace(".class", ""));
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
}
