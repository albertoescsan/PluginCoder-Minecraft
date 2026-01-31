package berty.plugincoder.interpreter.translators.classes.game;

import berty.plugincoder.interpreter.translators.classes.game.events.GameEventsMethodsTranslator;
import berty.plugincoder.interpreter.translators.classes.game.game.GameMethodsTranslator;
import berty.plugincoder.interpreter.translators.classes.game.team.TeamMethodsTranslator;

public class MinigameMethodsTranslator {

    public static void registerMethods(){
        GameEventsMethodsTranslator.registerMethods();
        GameMethodsTranslator.registerMethods();
        TeamMethodsTranslator.registerMethods();
    }
}
