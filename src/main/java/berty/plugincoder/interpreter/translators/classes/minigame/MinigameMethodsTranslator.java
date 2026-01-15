package berty.plugincoder.interpreter.translators.classes.minigame;

import berty.plugincoder.interpreter.translators.classes.minigame.events.GameEventsMethodsTranslator;
import berty.plugincoder.interpreter.translators.classes.minigame.game.GameMethodsTranslator;
import berty.plugincoder.interpreter.translators.classes.minigame.team.TeamMethodsTranslator;

public class MinigameMethodsTranslator {

    public static void registerMethods(){
        GameEventsMethodsTranslator.registerMethods();
        GameMethodsTranslator.registerMethods();
        TeamMethodsTranslator.registerMethods();
    }
}
