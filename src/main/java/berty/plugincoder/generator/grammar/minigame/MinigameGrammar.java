package berty.plugincoder.generator.grammar.minigame;

import berty.plugincoder.generator.grammar.Priority;
import berty.plugincoder.generator.grammar.plugin.PluginGrammar;
import berty.plugincoder.interpreter.command.CommandVarType;

import java.util.HashMap;
import java.util.Map;

public class MinigameGrammar extends PluginGrammar {
    @Priority(value = 0)
    public static void create(Map<String,String> data, int iteration){
        PluginGrammar.create(new HashMap<String,String>(){{put("NAME","Minigame");}},iteration);
        plugin.getMainObject().addProperty("games","[]");
        plugin.getMainObject().addProperty("gametemplate","GameTemplate");
        plugin.getMainObject().addProperty("mainlobby",null);
        plugin.getMainObject().getFunctions().add("game(name){for(game:games){if(game.name=name){return game;}}return null;}");
        plugin.addCommand("game create name",new HashMap<String, CommandVarType>(){{put("name",CommandVarType.TEXT);}},
                "game=plugin.game(name);if(game=null){plugin.games.add(gametemplate.build(name));sender.message(GREEN+Game +YELLOW+name+GREEN+ created);}");
        plugin.addCommand("game delete name",new HashMap<String, CommandVarType>(){{put("name",CommandVarType.TEXT);}},
                "game=plugin.game(name);if(game!=null){plugin.games.remove(game);sender.message(GREEN+Game +YELLOW+name+GREEN+ deleted);}");
        plugin.addCommand("game join name",new HashMap<String, CommandVarType>(){{put("name",CommandVarType.TEXT);}},
                "game=plugin.game(name);if(game!=null){game.join(sender);}");
        plugin.addCommand("game activate name",new HashMap<String, CommandVarType>(){{put("name",CommandVarType.TEXT);}},
                "game=plugin.game(name);if(game!=null){game.activate;}");
        plugin.addCommand("game deactivate name",new HashMap<String, CommandVarType>(){{put("name",CommandVarType.TEXT);}},
                "game=plugin.game(name);if(game!=null){game.deactivate;}");
    }
    @Priority(value = 1)
    public static void teams(Map<String,String> data,int iteration){
        int teams=Integer.valueOf(data.get("TEAMS"));
        TeamGrammar.teamsCreatedInEachIteration.add(teams);
        for(int i=0;i<teams;i++)TeamGrammar.add(data,iteration);
    }
}
