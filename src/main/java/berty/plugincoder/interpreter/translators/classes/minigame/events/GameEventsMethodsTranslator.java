package berty.plugincoder.interpreter.translators.classes.minigame.events;

import berty.plugincoder.interpreter.classes.minigame.events.*;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.ArrayList;
import java.util.Arrays;

public class GameEventsMethodsTranslator {

    public static void registerMethods(){
        PluginMethod game=new PluginMethod("game");
        game.getTranslatedMethodClasses().put("getGame",new ArrayList<>(Arrays.asList(PlayerJoinGameEvent.class.getTypeName(),
        PlayerLeaveGameEvent.class.getTypeName(), GameStartingEvent.class.getTypeName(), GameStartEvent.class.getTypeName(),
        GameFinishEvent.class.getTypeName())));
        PluginMethod player=PluginMethod.getMethod("player");
        player.getTranslatedMethodClasses().get("getPlayer").addAll(Arrays.asList(PlayerJoinGameEvent.class.getTypeName(),
                PlayerLeaveGameEvent.class.getTypeName()));
        PluginMethod message=PluginMethod.getMethod("message");
        message.getTranslatedMethodClasses().get("getMessage").addAll(new ArrayList<>(Arrays.asList(PlayerJoinGameEvent.class.getTypeName(),
                PlayerLeaveGameEvent.class.getTypeName(), GameStartingEvent.class.getTypeName(), GameStartEvent.class.getTypeName(),
                GameFinishEvent.class.getTypeName())));
        PluginMethod messageP=PluginMethod.getMethod("message()");
        messageP.getTranslatedMethodClasses().put("setMessage",new ArrayList<>(Arrays.asList(PlayerJoinGameEvent.class.getTypeName(),
                PlayerLeaveGameEvent.class.getTypeName(), GameStartEvent.class.getTypeName(),
                GameFinishEvent.class.getTypeName())));
        PluginMethod messages=new PluginMethod("messages");
        messages.getTranslatedMethodClasses().put("getMessages",Arrays.asList(GameStartingEvent.class.getTypeName()));

        PluginMethod time=new PluginMethod("time");
        time.getTranslatedMethodClasses().put("getTime",Arrays.asList(GameStartingEvent.class.getTypeName(),GameFinishEvent.class.getTypeName()));
        PluginMethod timeP=new PluginMethod("time()");
        timeP.getTranslatedMethodClasses().put("setTime",Arrays.asList(GameStartingEvent.class.getTypeName(),GameFinishEvent.class.getTypeName()));

        PluginMethod fireworks=new PluginMethod("fireworks");
        fireworks.getTranslatedMethodClasses().put("isFireworks",Arrays.asList(GameFinishEvent.class.getTypeName()));
        PluginMethod fireworksP=new PluginMethod("fireworks()");
        fireworksP.getTranslatedMethodClasses().put("setFireworks",Arrays.asList(GameFinishEvent.class.getTypeName()));
    }
}
