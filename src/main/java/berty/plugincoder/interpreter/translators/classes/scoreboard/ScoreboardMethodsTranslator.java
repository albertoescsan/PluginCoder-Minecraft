package berty.plugincoder.interpreter.translators.classes.scoreboard;

import berty.plugincoder.interpreter.classes.scoreboard.Scoreboard;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.Arrays;
import java.util.List;

public class ScoreboardMethodsTranslator {

    public static void registerMethods(){
        List<String> scoreboardClass= Arrays.asList(Scoreboard.class.getTypeName());
        PluginMethod.getMethod("clear").getTranslatedMethodClasses().get("clear").add(Scoreboard.class.getTypeName());
        PluginMethod line=new PluginMethod("line()");
        line.getTranslatedMethodClasses().put("addLine",scoreboardClass);
        PluginMethod title=new PluginMethod("title");
        title.getTranslatedMethodClasses().put("getTitle",scoreboardClass);
        PluginMethod titleP=new PluginMethod("title()");
        titleP.getTranslatedMethodClasses().put("setTitle",scoreboardClass);
        PluginMethod show=new PluginMethod("show");
        show.getTranslatedMethodClasses().put("show",scoreboardClass);
        PluginMethod hide=new PluginMethod("hide");
        hide.getTranslatedMethodClasses().put("hide",scoreboardClass);
    }
}
