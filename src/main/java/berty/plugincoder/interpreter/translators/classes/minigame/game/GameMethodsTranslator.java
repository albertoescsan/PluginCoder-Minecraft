package berty.plugincoder.interpreter.translators.classes.minigame.game;

import berty.plugincoder.interpreter.classes.minigame.game.Game;
import berty.plugincoder.interpreter.classes.minigame.game.GameTemplate;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameMethodsTranslator {

    public static void registerMethods(){
        List<String> gameClass=Arrays.asList(Game.class.getTypeName());
        PluginMethod build=new PluginMethod("build");
        build.getTranslatedMethodClasses().put("build",new ArrayList<>(Arrays.asList(GameTemplate.class.getTypeName())));

        PluginMethod name=PluginMethod.getMethod("name");
        name.getTranslatedMethodClasses().get("getName").add(Game.class.getTypeName());

        PluginMethod join=new PluginMethod("join");
        join.getTranslatedMethodClasses().put("join",gameClass);
        PluginMethod leave=new PluginMethod("leave");
        leave.getTranslatedMethodClasses().put("leave",gameClass);
        PluginMethod start=new PluginMethod("start");
        start.getTranslatedMethodClasses().put("start",gameClass);
        PluginMethod finish=new PluginMethod("finish");
        finish.getTranslatedMethodClasses().put("finish",gameClass);
        PluginMethod spawn=new PluginMethod("spawn");
        spawn.getTranslatedMethodClasses().put("getSpawn",gameClass);
        PluginMethod spawnP=new PluginMethod("spawn()");
        spawnP.getTranslatedMethodClasses().put("setSpawn",gameClass);

        PluginMethod activate=new PluginMethod("activate");
        activate.getTranslatedMethodClasses().put("activate",new ArrayList<>(Arrays.asList(Game.class.getTypeName())));
        PluginMethod deactivate=new PluginMethod("deactivate");
        deactivate.getTranslatedMethodClasses().put("deactivate",new ArrayList<>(Arrays.asList(Game.class.getTypeName())));

        PluginMethod maxPlayers=new PluginMethod("maxplayers");
        maxPlayers.getTranslatedMethodClasses().put("getMaxPlayers",new ArrayList<>(Arrays.asList(Game.class.getTypeName())));
        PluginMethod maxPlayersP=new PluginMethod("maxplayers()");
        maxPlayersP.getTranslatedMethodClasses().put("setMaxPlayers",new ArrayList<>(Arrays.asList(GameTemplate.class.getTypeName(),
                Game.class.getTypeName())));
        PluginMethod minPlayers=new PluginMethod("minplayers");
        minPlayers.getTranslatedMethodClasses().put("getMinPlayers",new ArrayList<>(Arrays.asList(Game.class.getTypeName())));
        PluginMethod minPlayersP=new PluginMethod("minplayers()");
        minPlayersP.getTranslatedMethodClasses().put("setMinPlayers",new ArrayList<>(Arrays.asList(GameTemplate.class.getTypeName(),
                Game.class.getTypeName())));
        PluginMethod.getMethod("scoreboard").getTranslatedMethodClasses().get("getScoreboard").addAll(Arrays.asList(GameTemplate.class.getTypeName(),
        Game.class.getTypeName()));

        PluginMethod teams=new PluginMethod("teams");
        teams.getTranslatedMethodClasses().put("getTeams",new ArrayList<>(Arrays.asList(GameTemplate.class.getTypeName(),
                Game.class.getTypeName())));
    }
}
