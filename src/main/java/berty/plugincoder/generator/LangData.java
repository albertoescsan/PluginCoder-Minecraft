package berty.plugincoder.generator;

import berty.plugincoder.interpreter.Language;
import berty.plugincoder.reader.PluginFileReader;

import java.util.*;
import java.util.stream.Collectors;

public class LangData {
    private List<String> plugins=new ArrayList<>();
    private List<String> entities=new ArrayList<>();

    private List<String> actions=new ArrayList<>();
    private Map<String,String> events=new HashMap<>();

    private Map<String,String> translator=new HashMap<>();
    private Map<String,String> actionNextGrammar=new HashMap<>();
    private Map<String,List<String>> grammar=new HashMap<>();
    private Map<String,String> colors=new HashMap<>();
    private Map<String,String> materials=new HashMap<>();
    public LangData(Language lang){
        List<String> lines=PluginFileReader.getLinesFromInternalFile("generator/"+lang.toString().toLowerCase()+"_data.txt");
        Object currentContainer=null;
        String contentContainerName = null;
        List<String> containerNames=Arrays.asList("plugins","entities","events","colors","materials","translator","action grammar");
        for(String line: lines){
            line=line.trim();if(line.isEmpty())continue;
            String[] contentToAdd=line.split(",");;
            if(line.matches("^([a-zA-Z\\s]+):(.*)$")){
                if(!(contentContainerName!=null&&contentContainerName.equalsIgnoreCase("action grammar"))){
                    contentContainerName=line.replaceAll("^([a-zA-Z\\s]+):(.*)$","$1");
                    if(containerNames.contains(contentContainerName.toLowerCase())){
                        currentContainer=getContentContainer(contentContainerName);
                        contentToAdd=line.replaceAll("^([a-zA-Z\\s]+):(.*)$","$2").split(",");
                    }
                }
            }
            for(String content:contentToAdd){
                content=content.trim();if(content.isEmpty())continue;
                if(currentContainer instanceof ArrayList<?>){((List<String>) currentContainer).add(content);continue;}
                String[] mapContent=content.split(":");

                if(contentContainerName.equalsIgnoreCase("action grammar")){
                    String mapContent1=mapContent[1].replaceAll("^(.+)=>\\s*([a-z]+)\\s*$","$1");
                    if(mapContent[1].matches("^(.+)=>\\s*([a-z]+)\\s*$")){
                        actionNextGrammar.put(mapContent[0].trim(),mapContent[1].replaceAll("^(.+)=>\\s*([a-z]+)\\s*$","$2").trim());
                    }for(String grammarOption:mapContent1.split("\\|")){
                        if(!grammarOption.contains("->"))continue;
                        String[] grammarAction=grammarOption.split("->");
                        actionNextGrammar.put(grammarAction[0].trim(),grammarAction[1].trim());
                    }
                    ((Map<String,List<String>>) currentContainer).put(mapContent[0],Arrays.asList(mapContent1.split("\\|"))
                            .stream().map(text->text.replaceAll("^(.+)->\\s*([a-z]+)\\s*$","$1").trim()).collect(Collectors.toList()));
                }else ((Map<String,String>) currentContainer).put(mapContent[0].trim(),mapContent[1].trim());
            }
        }
        actions.addAll(grammar.keySet());
    }

    private Object getContentContainer(String containerName) {
        if(containerName.toLowerCase().equals("plugins"))return plugins;
        if(containerName.toLowerCase().equals("entities"))return entities;
        if(containerName.toLowerCase().equals("events"))return events;
        if(containerName.toLowerCase().equals("colors"))return colors;
        if(containerName.toLowerCase().equals("materials"))return materials;

        if(containerName.toLowerCase().equals("translator"))return translator;
        if(containerName.toLowerCase().equals("action grammar"))return grammar;
        return null;
    }

    public List<String> getPlugins() {
        return plugins;
    }
    public List<String> getEntities() {
        return entities;
    }

    public List<String> getActions() {
        return actions;
    }

    public Map<String, String> getEvents() {return events;}

    public Map<String, String> getTranslator() {
        return translator;
    }

    public Map<String, String> getActionNextGrammar() {
        return actionNextGrammar;
    }

    public Map<String, List<String>> getGrammar() {
        return grammar;
    }

    public Map<String, String> getColors() {
        return colors;
    }

    public Map<String, String> getMaterials() {
        return materials;
    }
}
